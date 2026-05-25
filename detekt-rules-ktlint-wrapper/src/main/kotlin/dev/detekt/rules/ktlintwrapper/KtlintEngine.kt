package dev.detekt.rules.ktlintwrapper

import com.intellij.lang.ASTNode
import com.intellij.psi.impl.source.JavaDummyElement
import com.intellij.psi.impl.source.JavaDummyHolder
import com.pinterest.ktlint.rule.engine.core.api.RuleId
import com.pinterest.ktlint.ruleset.standard.StandardRule
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Location
import dev.detekt.api.SourceLocation
import dev.detekt.api.TextLocation
import dev.detekt.api.modifiedText
import dev.detekt.psi.absolutePath
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Coordinator that runs every enabled [KtlintRule] wrapping against each Kotlin file in a single
 * shared AST walk, replacing the previous per-rule design where detekt invoked a full PSI copy +
 * AST walk for every wrapped ktlint rule. The original design produced N walks (and N PSI copies)
 * per file — for a project with 720 source files and the default rule set that meant ~63 000
 * visit() invocations and several gigabytes of pinned PSI.
 *
 * Lifecycle:
 *  * [register] is invoked by [KtlintWrapperProvider] when detekt instantiates each rule.
 *  * [contextFor] lazily builds a per-file [FileContext] containing one PSI copy, the line/column
 *    index, and the findings produced by walking the AST once and dispatching each node to every
 *    registered wrapping (via fresh per-file [StandardRule] instances from [KtlintRule.newWrapping]
 *    so concurrent files do not race on shared ktlint state).
 *  * [ruleDoneWithFile] is invoked from [KtlintRule.visit] after each rule has consumed its
 *    findings; once the last rule has signalled, the context is evicted and the PSI copy can be
 *    garbage collected.
 */
internal object KtlintEngine {

    private val registry = mutableListOf<KtlintRule>()

    private val contexts = ConcurrentHashMap<KtFile, Lazy<FileContext>>()

    /**
     * Registers (or replaces) the canonical [KtlintRule] instance for its [RuleId]. Latest-wins so
     * that newly-instantiated rules with custom configuration take precedence over earlier
     * registrations — important for tests, which create fresh rule instances with bespoke configs
     * and expect the engine to honour them.
     */
    @Synchronized
    fun register(rule: KtlintRule) {
        val ruleId = rule.wrapping.ruleId
        val existing = registry.indexOfFirst { it.wrapping.ruleId == ruleId }
        if (existing >= 0) {
            registry[existing] = rule
        } else {
            registry.add(rule)
        }
    }

    fun contextFor(root: KtFile): FileContext = contexts.computeIfAbsent(root) { lazy { build(it) } }.value

    fun ruleDoneWithFile(root: KtFile, context: FileContext) {
        if (context.remainingVisits.decrementAndGet() <= 0) {
            contexts.remove(root)
        }
    }

    internal class FileContext(
        val fileCopy: KtFile,
        val findings: Map<RuleId, List<Finding>>,
        val remainingVisits: AtomicInteger,
    )

    private fun build(root: KtFile): FileContext {
        val fileCopy = KtPsiFactory(root.project).createPhysicalFile(
            root.name,
            root.modifiedText ?: root.text,
        )
        val positionByOffset = KtLintLineColCalculator.calculateLineColByOffset(fileCopy.text)
        val originalPath = root.absolutePath()

        // Restrict the shared walk to rules that are either active by config or have had their
        // visit() called directly (the test path via `subject.lint(...)`). This stops stale
        // provider-registered rules from participating — important for autocorrecting rules
        // whose AST mutations would otherwise leak into the test's modifiedText assertions.
        val active = snapshotRegistry().filter { it.triggeredVisit || it.isActiveByConfig() }
        if (active.isEmpty()) {
            return FileContext(fileCopy, emptyMap(), AtomicInteger(0))
        }
        // Fresh per-file ktlint StandardRule instances so different files dispatching through
        // the engine concurrently do not race on shared per-file state (counters, last-token, etc.).
        val perFile: List<Pair<KtlintRule, StandardRule>> = active.map { it to it.newWrapping() }

        perFile.forEach { (detektRule, ktlintRule) ->
            ktlintRule.beforeFirstNode(detektRule.computeEditorConfigProperties())
        }

        val findings = HashMap<RuleId, MutableList<Finding>>()
        walkOnce(fileCopy.node, perFile, findings, positionByOffset, originalPath)

        perFile.forEach { (_, ktlintRule) -> ktlintRule.afterLastNode() }

        if (fileCopy.modificationStamp > 0) {
            root.modifiedText = fileCopy.text
        }

        return FileContext(fileCopy, findings, AtomicInteger(active.size))
    }

    private fun snapshotRegistry(): List<KtlintRule> = synchronized(this) { registry.toList() }

    private fun KtlintRule.isActiveByConfig(): Boolean = config.valueOrDefault("active", false)

    private fun walkOnce(
        node: ASTNode,
        rules: List<Pair<KtlintRule, StandardRule>>,
        findings: HashMap<RuleId, MutableList<Finding>>,
        positionByOffset: (Int) -> Pair<Int, Int>,
        originalPath: Path,
    ) {
        if (node.isNotDummyElement()) {
            for ((detektRule, ktlintRule) in rules) {
                ktlintRule.beforeVisitChildNodes(node) { offset, message, canBeAutoCorrected ->
                    appendFinding(
                        node, offset, message, canBeAutoCorrected, detektRule, ktlintRule,
                        findings, positionByOffset, originalPath,
                    )
                    detektRule.autocorrectDecision
                }
            }
        }
        val children = node.getChildren(null)
        for (i in children.indices) {
            walkOnce(children[i], rules, findings, positionByOffset, originalPath)
        }
        if (node.isNotDummyElement()) {
            for ((detektRule, ktlintRule) in rules) {
                ktlintRule.afterVisitChildNodes(node) { offset, message, canBeAutoCorrected ->
                    appendFinding(
                        node, offset, message, canBeAutoCorrected, detektRule, ktlintRule,
                        findings, positionByOffset, originalPath,
                    )
                    detektRule.autocorrectDecision
                }
            }
        }
    }

    @Suppress("LongParameterList")
    private fun appendFinding(
        node: ASTNode,
        offset: Int,
        message: String,
        canBeAutoCorrected: Boolean,
        detektRule: KtlintRule,
        ktlintRule: StandardRule,
        findings: HashMap<RuleId, MutableList<Finding>>,
        positionByOffset: (Int) -> Pair<Int, Int>,
        originalPath: Path,
    ) {
        // Construct the Finding *now* — node.psi is still attached to the tree. Deferring this
        // until KtlintRule.visit() runs (after the walk completes) was unsafe: autocorrecting
        // rules mutate the AST mid-walk and may detach previously-visited nodes' PSI by the
        // time the per-rule visit consumes the findings. Build it eagerly and store an
        // immutable Finding instead.
        val psi = node.psi ?: return
        val (line, column) = positionByOffset(offset)
        val location = Location(
            source = SourceLocation(line, column),
            endSource = SourceLocation(line, column),
            text = TextLocation(offset, offset + 1),
            path = originalPath,
        )
        // Entity.from walks parents to locate a KtElement; the leaf psi at `node` may not be
        // one itself but is normally inside a KtElement (function, expression, ...).
        val entity = Entity.from(psi, location)
        val finding = if (canBeAutoCorrected && detektRule.autoCorrect) {
            Finding(entity, message, suppressReasons = listOf("Auto correct"))
        } else {
            Finding(entity, message)
        }
        findings.getOrPut(ktlintRule.ruleId) { mutableListOf() }.add(finding)
    }

    private fun ASTNode.isNotDummyElement(): Boolean {
        val parent = this.psi?.parent
        return parent !is JavaDummyHolder && parent !is JavaDummyElement
    }
}

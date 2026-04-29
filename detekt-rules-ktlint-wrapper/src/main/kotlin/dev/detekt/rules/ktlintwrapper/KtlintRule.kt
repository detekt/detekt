package dev.detekt.rules.ktlintwrapper

import com.intellij.lang.ASTNode
import com.pinterest.ktlint.rule.engine.core.api.AutocorrectDecision
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.CODE_STYLE_PROPERTY
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfig
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.INDENT_STYLE_PROPERTY
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.createRuleExecutionEditorConfigProperty
import com.pinterest.ktlint.ruleset.standard.StandardRule
import com.pinterest.ktlint.ruleset.standard.rules.MAX_LINE_LENGTH_RULE_ID
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Location
import dev.detekt.api.Rule
import dev.detekt.api.SourceLocation
import dev.detekt.api.TextLocation
import dev.detekt.psi.absolutePath
import org.ec4j.core.model.Property
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path

/**
 * Rule to detect formatting violations.
 *
 * Subclasses delegate the actual ktlint dispatch to [KtlintEngine]; each rule's [visit] simply
 * consumes the findings the engine collected during the shared per-file walk.
 */
abstract class KtlintRule(config: Config, description: String) : Rule(config, description) {

    abstract val wrapping: StandardRule

    /**
     * Factory for fresh per-file [StandardRule] instances. [KtlintEngine] uses this so each
     * Kotlin file gets its own ktlint rule instance, preventing cross-thread races on the
     * wrapping's internal per-file state (counters, last-token, etc.). The default reflective
     * implementation works for the standard ktlint rule set; override if the wrapped
     * [StandardRule] has a non-default constructor.
     */
    open fun newWrapping(): StandardRule = wrapping::class.java.getDeclaredConstructor().newInstance()

    internal val autocorrectDecision: AutocorrectDecision
        get() = if (autoCorrect) AutocorrectDecision.ALLOW_AUTOCORRECT else AutocorrectDecision.NO_AUTOCORRECT

    protected val codeStyle: String
        get() = config.valueOrNull("code_style")
            ?: config.parent?.let { KtlintWrapperProvider.code_style.value(it) }
            ?: KtlintWrapperProvider.code_style.defaultValue

    private lateinit var positionByOffset: (offset: Int) -> Pair<Int, Int>
    private lateinit var root: KtFile
    private lateinit var originalFilePath: Path

    override fun visit(root: KtFile) {
        val context = KtlintEngine.contextFor(root)
        try {
            val myFindings = context.findings[wrapping.ruleId]
            if (!myFindings.isNullOrEmpty()) {
                this.root = context.fileCopy
                originalFilePath = root.absolutePath()
                positionByOffset = context.positionByOffset
                myFindings.forEach { finding ->
                    emitFinding(finding.offset, finding.message, finding.canBeAutoCorrected, finding.node)
                }
            }
        } finally {
            KtlintEngine.ruleDoneWithFile(root, context)
        }
    }

    open fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String>? = null

    internal fun computeEditorConfigProperties(): EditorConfig {
        val usesEditorConfigProperties = overrideEditorConfigProperties()?.toMutableMap()
            ?: mutableMapOf()

        /* Notify ktlint that max line length rule is enabled so the max line length configuration for each rule is
           respected: https://github.com/pinterest/ktlint/pull/2783 */
        usesEditorConfigProperties[MAX_LINE_LENGTH_RULE_ID.createRuleExecutionEditorConfigProperty()] = "enabled"

        usesEditorConfigProperties[CODE_STYLE_PROPERTY] = codeStyle

        usesEditorConfigProperties[INDENT_STYLE_PROPERTY] = usesEditorConfigProperties.getOrDefault(
            INDENT_STYLE_PROPERTY,
            "space",
        )

        val properties = buildMap {
            usesEditorConfigProperties.forEach { (editorConfigProperty, defaultValue) ->
                put(
                    key = editorConfigProperty.name,
                    value = Property.builder()
                        .name(editorConfigProperty.type.name)
                        .type(editorConfigProperty.type)
                        .value(defaultValue)
                        .build()
                )
            }
        }

        return EditorConfig(properties)
    }

    private fun emitFinding(offset: Int, message: String, canBeAutoCorrected: Boolean, node: ASTNode) {
        // Always convert KtLint offsets to lines/columns.
        // The node used to report the finding may be not the same used for the offset (e.g. in NoUnusedImports).
        val (line, column) = positionByOffset(offset)
        val location = Location(
            source = SourceLocation(line, column),
            endSource = SourceLocation(line, column),
            // Use offset + 1 since ktlint always reports a single location.
            text = TextLocation(offset, offset + 1),
            path = originalFilePath
        )
        val entity = Entity.from(node.psi, location)

        if (canBeAutoCorrected && autoCorrect) {
            report(Finding(entity, message, suppressReasons = listOf("Auto correct")))
        } else {
            report(Finding(entity, message))
        }
    }
}

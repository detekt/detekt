package io.gitlab.arturbosch.detekt.formatting

import com.pinterest.ktlint.rule.engine.core.api.Rule.VisitorModifier.RunAsLateAsPossible
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.CODE_STYLE_PROPERTY
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfig
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.INDENT_STYLE_PROPERTY
import io.github.detekt.psi.toFilePath
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.CorrectableCodeSmell
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SingleAssign
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
import org.ec4j.core.model.Property
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.JavaDummyElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.JavaDummyHolder
import org.jetbrains.kotlin.psi.KtFile

/**
 * Rule to detect formatting violations.
 */
abstract class FormattingRule(config: Config) : Rule(config) {

    abstract val wrapping: com.pinterest.ktlint.rule.engine.core.api.Rule

    /**
     * Should the android style guide be enforced?
     * This property is read from the ruleSet config.
     */
    protected val isAndroid
        get() = FormattingProvider.android.value(ruleSetConfig)

    val runAsLateAsPossible
        get() = RunAsLateAsPossible in wrapping.visitorModifiers

    private var positionByOffset: (offset: Int) -> Pair<Int, Int> by SingleAssign()
    private var root: KtFile by SingleAssign()

    protected fun issueFor(description: String) =
        Issue(javaClass.simpleName, Severity.Style, description, Debt.FIVE_MINS)

    override fun visit(root: KtFile) {
        this.root = root
        positionByOffset = KtLintLineColCalculator
            .calculateLineColByOffset(KtLintLineColCalculator.normalizeText(root.text))

        wrapping.beforeFirstNode(computeEditorConfigProperties())
        root.node.visitASTNodes()
        wrapping.afterLastNode()
    }

    open fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String>? = null

    private fun computeEditorConfigProperties(): EditorConfig {
        val usesEditorConfigProperties = overrideEditorConfigProperties()?.toMutableMap()
            ?: mutableMapOf()

        if (isAndroid) {
            usesEditorConfigProperties[CODE_STYLE_PROPERTY] = "android_studio"
        } else {
            usesEditorConfigProperties[CODE_STYLE_PROPERTY] = "intellij_idea"
        }

        usesEditorConfigProperties[INDENT_STYLE_PROPERTY] = "space"

        val properties = buildMap {
            usesEditorConfigProperties.forEach { (editorConfigProperty, defaultValue) ->
                put(
                    key = editorConfigProperty.type.name,
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
            SourceLocation(line, column),
            // Use offset + 1 since ktlint always reports a single location.
            TextLocation(offset, offset + 1),
            root.toFilePath()
        )
        val entity = Entity.from(node.psi, location)

        if (canBeAutoCorrected) {
            report(CorrectableCodeSmell(issue, entity, message, autoCorrectEnabled = autoCorrect))
        } else {
            report(CodeSmell(issue, entity, message))
        }
    }

    private fun beforeVisitChildNodes(node: ASTNode) {
        wrapping.beforeVisitChildNodes(node, autoCorrect) { offset, errorMessage, canBeAutoCorrected ->
            emitFinding(offset, errorMessage, canBeAutoCorrected, node)
        }
    }

    private fun afterVisitChildNodes(node: ASTNode) {
        wrapping.afterVisitChildNodes(node, autoCorrect) { offset, errorMessage, canBeAutoCorrected ->
            emitFinding(offset, errorMessage, canBeAutoCorrected, node)
        }
    }

    private fun ASTNode.visitASTNodes() {
        if (isNotDummyElement()) {
            beforeVisitChildNodes(this)
        }
        getChildren(null).forEach {
            it.visitASTNodes()
        }
        if (isNotDummyElement()) {
            afterVisitChildNodes(this)
        }
    }

    private fun ASTNode.isNotDummyElement(): Boolean {
        val parent = this.psi?.parent
        return parent !is JavaDummyHolder && parent !is JavaDummyElement
    }
}

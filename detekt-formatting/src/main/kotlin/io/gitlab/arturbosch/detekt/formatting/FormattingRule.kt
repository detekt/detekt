package io.gitlab.arturbosch.detekt.formatting

import com.pinterest.ktlint.core.Rule.VisitorModifier.RunAsLateAsPossible
import com.pinterest.ktlint.core.api.EditorConfigProperties
import com.pinterest.ktlint.core.api.editorconfig.CODE_STYLE_PROPERTY
import com.pinterest.ktlint.core.api.editorconfig.EditorConfigProperty
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.CorrectableCodeSmell
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SingleAssign
import org.ec4j.core.model.Property
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.JavaDummyElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.JavaDummyHolder
import org.jetbrains.kotlin.psi.KtFile

/**
 * Rule to detect formatting violations.
 */
abstract class FormattingRule(config: Config) : Rule(config) {

    abstract val wrapping: com.pinterest.ktlint.core.Rule

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

    private fun computeEditorConfigProperties(): EditorConfigProperties {
        val usesEditorConfigProperties = overrideEditorConfigProperties()?.toMutableMap()
            ?: mutableMapOf()

        if (isAndroid) {
            usesEditorConfigProperties[CODE_STYLE_PROPERTY] = "android"
        }

        return buildMap {
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
    }

    private fun emitFinding(message: String, canBeAutoCorrected: Boolean, node: ASTNode) {
        val entity = Entity.from(node.psi)

        if (canBeAutoCorrected) {
            report(CorrectableCodeSmell(issue, entity, message, autoCorrectEnabled = autoCorrect))
        } else {
            report(CodeSmell(issue, entity, message))
        }
    }

    private fun beforeVisitChildNodes(node: ASTNode) {
        wrapping.beforeVisitChildNodes(node, autoCorrect) { _, errorMessage, canBeAutoCorrected ->
            emitFinding(errorMessage, canBeAutoCorrected, node)
        }
    }

    private fun afterVisitChildNodes(node: ASTNode) {
        wrapping.afterVisitChildNodes(node, autoCorrect) { _, errorMessage, canBeAutoCorrected ->
            emitFinding(errorMessage, canBeAutoCorrected, node)
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

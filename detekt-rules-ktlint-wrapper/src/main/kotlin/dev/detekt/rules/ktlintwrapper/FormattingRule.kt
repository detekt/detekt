package dev.detekt.rules.ktlintwrapper

import com.intellij.lang.ASTNode
import com.intellij.psi.impl.source.JavaDummyElement
import com.intellij.psi.impl.source.JavaDummyHolder
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
import dev.detekt.api.modifiedText
import dev.detekt.psi.absolutePath
import org.ec4j.core.model.Property
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import java.nio.file.Path

/**
 * Rule to detect formatting violations.
 */
abstract class FormattingRule(config: Config, description: String) : Rule(config, description) {

    abstract val wrapping: StandardRule

    protected val codeStyle: String
        get() = config.valueOrNull("code_style")
            ?: config.parent?.let { FormattingProvider.code_style.value(it) }
            ?: FormattingProvider.code_style.defaultValue

    private lateinit var positionByOffset: (offset: Int) -> Pair<Int, Int>
    private lateinit var root: KtFile
    private lateinit var originalFilePath: Path

    override fun visit(root: KtFile) {
        val fileCopy = KtPsiFactory(root.project).createPhysicalFile(root.name, root.modifiedText ?: root.text)

        this.root = fileCopy
        originalFilePath = root.absolutePath()
        positionByOffset = KtLintLineColCalculator.calculateLineColByOffset(fileCopy.text)

        wrapping.beforeFirstNode(computeEditorConfigProperties())
        this.root.node.visitASTNodes()
        wrapping.afterLastNode()

        if (this.root.modificationStamp > 0) {
            root.modifiedText = this.root.text
        }
    }

    open fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String>? = null

    private fun computeEditorConfigProperties(): EditorConfig {
        val usesEditorConfigProperties = overrideEditorConfigProperties()?.toMutableMap()
            ?: mutableMapOf()

        /* Notify ktlint that max line length rule is enabled so the max line length configuration for each rule is
           respected: https://github.com/pinterest/ktlint/pull/2783 */
        usesEditorConfigProperties[MAX_LINE_LENGTH_RULE_ID.createRuleExecutionEditorConfigProperty()] = "enabled"

        usesEditorConfigProperties[CODE_STYLE_PROPERTY] = codeStyle

        usesEditorConfigProperties[INDENT_STYLE_PROPERTY] = "space"

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

    private fun beforeVisitChildNodes(node: ASTNode) {
        wrapping.beforeVisitChildNodes(node) { offset, errorMessage, canBeAutoCorrected ->
            emitFinding(offset, errorMessage, canBeAutoCorrected, node)
            if (autoCorrect) AutocorrectDecision.ALLOW_AUTOCORRECT else AutocorrectDecision.NO_AUTOCORRECT
        }
    }

    private fun afterVisitChildNodes(node: ASTNode) {
        wrapping.afterVisitChildNodes(node) { offset, errorMessage, canBeAutoCorrected ->
            emitFinding(offset, errorMessage, canBeAutoCorrected, node)
            if (autoCorrect) AutocorrectDecision.ALLOW_AUTOCORRECT else AutocorrectDecision.NO_AUTOCORRECT
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

package io.gitlab.arturbosch.detekt.formatting

import com.pinterest.ktlint.core.KtLint
import com.pinterest.ktlint.core.Rule.VisitorModifier.RunAfterRule
import com.pinterest.ktlint.core.Rule.VisitorModifier.RunAsLateAsPossible
import com.pinterest.ktlint.core.Rule.VisitorModifier.RunOnRootNodeOnly
import com.pinterest.ktlint.core.api.DefaultEditorConfigProperties.codeStyleSetProperty
import com.pinterest.ktlint.core.api.UsesEditorConfigProperties
import io.github.detekt.psi.fileName
import io.github.detekt.psi.toFilePath
import io.gitlab.arturbosch.detekt.api.*
import org.ec4j.core.model.Property
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.lang.FileASTNode
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull

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

    val runOnRootNodeOnly
        get() = RunOnRootNodeOnly in wrapping.visitorModifiers

    val runAsLateAsPossible
        get() = RunAsLateAsPossible in wrapping.visitorModifiers

    val runAfterRule
        get() = wrapping.visitorModifiers.firstIsInstanceOrNull<RunAfterRule>()

    private var positionByOffset: (offset: Int) -> Pair<Int, Int> by SingleAssign()
    private var root: KtFile by SingleAssign()

    protected fun issueFor(description: String) =
        Issue(javaClass.simpleName, Severity.Style, description, Debt.FIVE_MINS)

    override fun visit(root: KtFile) {
        this.root = root
        positionByOffset = KtLintLineColCalculator
            .calculateLineColByOffset(KtLintLineColCalculator.normalizeText(root.text))

        val editorConfigProperties = overrideEditorConfigProperties()?.toMutableMap()
            ?: mutableMapOf()

        if (isAndroid) {
            editorConfigProperties[codeStyleSetProperty] = "android"
        }

        if (editorConfigProperties.isNotEmpty()) {
            val userData = (root.node.getUserData(KtLint.EDITOR_CONFIG_PROPERTIES_USER_DATA_KEY).orEmpty())
                .toMutableMap()

            editorConfigProperties.forEach { (editorConfigProperty, defaultValue) ->
                userData[editorConfigProperty.type.name] = Property.builder()
                    .name(editorConfigProperty.type.name)
                    .type(editorConfigProperty.type)
                    .value(defaultValue)
                    .build()
            }
            root.node.putUserData(KtLint.EDITOR_CONFIG_PROPERTIES_USER_DATA_KEY, userData)
        }
        root.node.putUserData(KtLint.FILE_PATH_USER_DATA_KEY, root.name)
    }

    open fun overrideEditorConfigProperties(): Map<UsesEditorConfigProperties.EditorConfigProperty<*>, String>? = null

    fun apply(node: ASTNode) {
        if (ruleShouldOnlyRunOnFileNode(node)) {
            return
        }

        wrapping.visit(node, autoCorrect) { offset, message, _ ->
            val (line, column) = positionByOffset(offset)
            val location = Location(
                SourceLocation(line, column),
                getTextLocationForViolation(node, offset),
                root.toFilePath()
            )

            // Nodes reported by 'NoConsecutiveBlankLines' are dangling whitespace nodes which means they have
            // no direct parent which we can use to get the containing file needed to baseline or suppress findings.
            // For these reasons we do not report a KtElement which may lead to crashes when postprocessing it
            // e.g. reports (html), baseline etc.
            val packageName = root.packageFqName.asString()
                .takeIf { it.isNotEmpty() }
                ?.plus(".")
                .orEmpty()
            val entity = Entity("", "$packageName${root.fileName}:$line", location, root)
            report(CorrectableCodeSmell(issue, entity, message, autoCorrectEnabled = autoCorrect))
        }
    }

    open fun getTextLocationForViolation(node: ASTNode, offset: Int) =
        TextLocation(node.startOffset, node.psi.endOffset)

    private fun ruleShouldOnlyRunOnFileNode(node: ASTNode) =
        runOnRootNodeOnly && node !is FileASTNode
}

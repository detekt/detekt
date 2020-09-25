package io.gitlab.arturbosch.detekt.formatting

import com.pinterest.ktlint.core.EditorConfig
import com.pinterest.ktlint.core.KtLint
import io.github.detekt.psi.absolutePath
import io.github.detekt.psi.fileName
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
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.lang.FileASTNode
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.endOffset

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
        get() = ruleSetConfig.valueOrDefault("android", false)

    private var positionByOffset: (offset: Int) -> Pair<Int, Int> by SingleAssign()
    private var root: KtFile by SingleAssign()

    protected fun issueFor(description: String) =
        Issue(javaClass.simpleName, Severity.Style, description, Debt.FIVE_MINS)

    override fun visit(root: KtFile) {
        this.root = root
        root.node.putUserData(KtLint.ANDROID_USER_DATA_KEY, isAndroid)
        positionByOffset = KtLint.calculateLineColByOffset(KtLint.normalizeText(root.text))
        editorConfigUpdater()?.let { updateFunc ->
            val oldEditorConfig = root.node.getUserData(KtLint.EDITOR_CONFIG_USER_DATA_KEY)
            root.node.putUserData(KtLint.EDITOR_CONFIG_USER_DATA_KEY, updateFunc(oldEditorConfig))
        }
        root.node.putUserData(KtLint.FILE_PATH_USER_DATA_KEY, root.name)
    }

    open fun editorConfigUpdater(): ((oldEditorConfig: EditorConfig?) -> EditorConfig)? = null

    fun apply(node: ASTNode) {
        if (ruleShouldOnlyRunOnFileNode(node)) {
            return
        }
        wrapping.visit(node, autoCorrect) { offset, message, _ ->
            val (line, column) = positionByOffset(offset)
            val location = Location(
                SourceLocation(line, column),
                TextLocation(node.startOffset, node.psi.endOffset),
                root.absolutePath().toString()
            )

            // Nodes reported by 'NoConsecutiveBlankLines' are dangling whitespace nodes which means they have
            // no direct parent which we can use to get the containing file needed to baseline or suppress findings.
            // For these reasons we do not report a KtElement which may lead to crashes when postprocessing it
            // e.g. reports (html), baseline etc.
            val packageName = root.packageFqName.asString()
                .takeIf { it.isNotEmpty() }
                ?.plus(".")
                ?: ""
            val entity = Entity("", "$packageName${root.fileName}:$line", location, root)
            report(CorrectableCodeSmell(issue, entity, message, autoCorrectEnabled = autoCorrect))
        }
    }

    private fun ruleShouldOnlyRunOnFileNode(node: ASTNode) =
        wrapping is com.pinterest.ktlint.core.Rule.Modifier.RestrictToRoot && node !is FileASTNode
}

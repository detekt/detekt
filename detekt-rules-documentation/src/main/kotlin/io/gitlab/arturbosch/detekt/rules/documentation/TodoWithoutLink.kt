package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.psiUtil.nextSiblingOfSameType

/**
 * Checks if the To-Do contains linked ticket
 */
class TodoWithoutLink(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(
        id = "TodoWithoutLink",
        description = "Checks if the link was added for ToDo",
        severity = Severity.Maintainability,
        debt = Debt.FIVE_MINS
    )

    @Configuration("custom link regex pattern")
    private val linkRegexPattern: String by config(DEFAULT_LINK_PATTERN)

    private var pendingCommentWithOffset: Pair<PsiComment, Int>? = null

    /** Visit only docs like this one */
    override fun visitDeclaration(dcl: KtDeclaration) {
        super.visitDeclaration(dcl)
        val kDoc = dcl.docComment ?: return
        validateBlockComment(kDoc)
    }

    // Visit only comments like this one
    override fun visitComment(comment: PsiComment) {
        super.visitComment(comment)
        if (comment.tokenType.toString() == "EOL_COMMENT") {
            validateEOLComment(comment)
        } else {
            validateBlockComment(comment)
        }
    }

    private fun validateBlockComment(comment: PsiComment) {
        val lines = comment.text.lines()
        var offset = 0
        lines.forEachIndexed { index, line ->
            offset += line.length
            validate(comment, line, hasContinuationOnNewLine = { index < lines.lastIndex }, offset)
            offset += 1
        }
    }

    private fun validateEOLComment(comment: PsiComment) {
        val line = comment.text
        validate(comment, line, { comment.hasContinuationOnNewLine() }, offset = line.length)
    }

    private fun validate(comment: PsiComment, line: String, hasContinuationOnNewLine: () -> Boolean, offset: Int) {
        when {
            line.containsTodoWithoutTicket() -> {
                if (hasContinuationOnNewLine()) {
                    pendingCommentWithOffset = comment to offset
                } else {
                    report(comment, offset)
                }
            }
            pendingCommentWithOffset != null && line.containsTicket() -> {
                pendingCommentWithOffset = null
            }
            pendingCommentWithOffset != null && !hasContinuationOnNewLine() -> {
                pendingCommentWithOffset?.let { report(it.first, it.second) }
            }
        }
    }

    private fun String.containsTodoWithoutTicket(): Boolean =
        contains("todo", ignoreCase = true) && !containsTicket()

    private fun String.containsTicket(): Boolean = linkRegexPattern.toRegex() in this

    private fun report(element: PsiComment, offset: Int) {
        report(CodeSmell(issue, Entity.from(element, offset), message = "$element has TODO without linked ticket."))
    }

    companion object {

        private const val DEFAULT_LINK_PATTERN = """\bhttps?://(?:www\d?)?[\w/#&?=\-.]+\b"""
    }
}

private val PsiElement.lineNumber: Int
    get() = containingFile.viewProvider.document?.getLineNumber(textRange.startOffset)?.plus(1) ?: -1

private fun PsiComment.hasContinuationOnNewLine(): Boolean {
    val nextComment = nextSiblingOfSameType() ?: return false
    return containingFile == nextComment.containingFile && lineNumber + 1 == nextComment.lineNumber
}

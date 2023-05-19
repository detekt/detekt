package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.valuesWithReason
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.kdoc.psi.impl.KDocSection
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

/**
 * This rule allows to set a list of comments which are forbidden in the codebase and should only be used during
 * development. Offending code comments will then be reported.
 *
 * <noncompliant>
 * val a = "" // TODO: remove please
 * // FIXME: this is a hack
 * fun foo() { }
 * // STOPSHIP:
 * </noncompliant>
 */
@ActiveByDefault(since = "1.0.0")
class ForbiddenComment(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Flags a forbidden comment.",
        Debt.TEN_MINS
    )

    @Configuration("forbidden comment strings")
    @Deprecated("Use `comments` instead, make sure you escape your text for Regular Expressions.")
    private val values: List<String> by config(emptyList())

    @Configuration("forbidden comment string patterns")
    private val comments: List<Comment> by config(
        valuesWithReason(
            "FIXME:" to "some fixes are pending.",
            "STOPSHIP:" to "some changes are present which needs to be addressed before ship.",
            "TODO:" to "some changes are pending.",
        )
    ) { list ->
        list.map { Comment(it.value.toRegex(), it.reason) }
    }

    @Configuration("ignores comments which match the specified regular expression. For example `Ticket|Task`.")
    private val allowedPatterns: Regex by config("", String::toRegex)

    @Configuration("error message which overrides the default one")
    @Deprecated("Use `comments` and provide `reason` against each `value`")
    private val customMessage: String by config("")

    override fun visitComment(comment: PsiComment) {
        super.visitComment(comment)
        val text = comment.getContent()
        checkForbiddenComment(text, comment)
    }

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        file.collectDescendantsOfType<KDocSection>().forEach { comment ->
            val text = comment.getContent()
            checkForbiddenComment(text, comment)
        }
    }

    private fun checkForbiddenComment(text: String, comment: PsiElement) {
        if (allowedPatterns.pattern.isNotEmpty() && allowedPatterns.containsMatchIn(text)) return

        @Suppress("DEPRECATION")
        values.forEach {
            if (text.contains(it, ignoreCase = true)) {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(comment),
                        getErrorMessage(it)
                    )
                )
            }
        }

        comments.forEach {
            if (it.value.containsMatchIn(text)) {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(comment),
                        getErrorMessage(it)
                    )
                )
            }
        }
    }

    private fun PsiComment.getContent(): String = text.getCommentContent()

    private fun getErrorMessage(comment: Comment): String =
        comment.reason
            ?.let { reason -> String.format(DEFAULT_ERROR_MESSAGE, comment.value.pattern, reason) }
            ?: String.format(DEFAULT_ERROR_MESSAGE_WITH_NO_REASON, comment.value.pattern)

    @Suppress("DEPRECATION")
    private fun getErrorMessage(value: String): String =
        customMessage.takeUnless { it.isEmpty() } ?: String.format(DEFAULT_ERROR_MESSAGE_WITH_NO_REASON, value)

    private data class Comment(val value: Regex, val reason: String?)

    companion object {
        const val DEFAULT_ERROR_MESSAGE_WITH_NO_REASON = "This comment contains '%s' " +
            "that has been defined as forbidden in detekt."

        const val DEFAULT_ERROR_MESSAGE = "This comment contains '%s' " +
            "that has been forbidden: %s."
    }
}

internal fun String.getCommentContent(): String {
    return if (this.startsWith("//")) {
        this.removePrefix("//").removePrefix(" ")
    } else {
        this
            .trimIndentIgnoringFirstLine()
            // Process line by line.
            .lineSequence()
            // Remove starting, aligning and ending markers.
            .map {
                it
                    .let { fullLine ->
                        val trimmedStartLine = fullLine.trimStart()
                        if (trimmedStartLine.startsWith("/*")) {
                            trimmedStartLine.removePrefix("/*").removePrefix(" ")
                        } else if (trimmedStartLine.startsWith("*") && trimmedStartLine.startsWith("*/").not()) {
                            trimmedStartLine.removePrefix("*").removePrefix(" ")
                        } else {
                            fullLine
                        }
                    }
                    .let { lineWithoutStartMarker ->
                        if (lineWithoutStartMarker.endsWith("*/")) {
                            lineWithoutStartMarker.removeSuffix("*/").removeSuffix(" ")
                        } else {
                            lineWithoutStartMarker
                        }
                    }
            }
            // Trim trailing empty lines.
            .dropWhile(String::isEmpty)
            // Reconstruct the comment contents.
            .joinToString("\n")
    }
}

private fun String.trimIndentIgnoringFirstLine(): String =
    if ('\n' !in this) {
        this
    } else {
        val lines = this.lineSequence()
        lines.first() + "\n" + lines.drop(1).joinToString("\n").trimIndent()
    }

package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.valuesWithReason
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.kdoc.psi.impl.KDocSection
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import java.util.Locale

// Note: ​ (zero-width-space) is used to prevent the Kotlin parser getting confused by talking about comments in a comment.

/**
 * This rule allows to set a list of comments which are forbidden in the codebase and should only be used during
 * development. Offending code comments will then be reported.
 *
 * The regular expressions in `comments` list will have the following behaviors while matching the comments:
 *  * **Each comment will be handled individually.**
 *    * single line comments are always separate, consecutive lines are not merged.
 *    * multi line comments are not split up, the regex will be applied to the whole comment.
 *    * KDoc comments are not split up, the regex will be applied to the whole comment.
 *  * **The following comment delimiters (and indentation before them) are removed** before applying the regex:
 *    `//`, `// `, `/​*`, `/​* `, `/​**`, `*` aligners, `*​/`, ` *​/`
 *  * **The regex is applied as a multiline regex**,
 *    see [Anchors](https://www.regular-expressions.info/anchors.html) for more info.
 *    To match the start and end of each line, use `^` and `$`.
 *    To match the start and end of the whole comment, use `\A` and `\Z`.
 *    To turn off multiline, use `(?-m)` at the start of your regex.
 *  * **The regex is applied with dotall semantics**, meaning `.` will match any character including newlines,
 *    this is to ensure that freeform line-wrapping doesn't mess with simple regexes.
 *    To turn off this behavior, use `(?-s)` at the start of your regex, or use `[^\r\n]*` instead of `.*`.
 *  * **The regex will be searched using "contains" semantics** not "matches",
 *    so partial comment matches will flag forbidden comments.
 *    In practice this means there's no need to start and end the regex with `.*`.
 *
 * The rule can be configured to add extra comments to the list of forbidden comments, here are some examples:
 * ```yaml
 *   ForbiddenComment:
 *     comments:
 *       # Repeat the default configuration if it's still needed.
 *       - reason: 'Forbidden FIXME todo marker in comment, please fix the problem.'
 *         value: 'FIXME:'
 *       - reason: 'Forbidden STOPSHIP todo marker in comment, please address the problem before shipping the code.'
 *         value: 'STOPSHIP:'
 *       - reason: 'Forbidden TODO todo marker in comment, please do the changes.'
 *         value: 'TODO:'
 *       # Add additional patterns to the list.
 *
 *       - reason: 'Authors are not recorded in KDoc.'
 *         value: '@author'
 *
 *       - reason: 'REVIEW markers are not allowed in production code, only use before PR is merged.'
 *         value: '^\s*(?i)REVIEW\b'
 *         # Non-compliant: // REVIEW this code before merging.
 *         # Compliant: // Preview will show up here.
 *
 *       - reason: 'Use @androidx.annotation.VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) instead.'
 *         value: '^private$'
 *         # Non-compliant: /*private*/ fun f() { }
 *
 *       - reason: 'KDoc tag should have a value.'
 *         value: '^\s*@(?!suppress|hide)\w+\s*$'
 *         # Non-compliant: /** ... @see */
 *         # Compliant: /** ... @throws IOException when there's a network problem */
 *
 *       - reason: 'include an issue link at the beginning preceded by a space'
 *         value: 'BUG:(?! https://github\.com/company/repo/issues/\d+).*'
 * ```
 *
 * By default the commonly used todo markers are forbidden: `TODO:`, `FIXME:` and `STOPSHIP:`.
 *
 * <noncompliant>
 * val a = "" // TODO: remove please
 * /**
 *  * FIXME: this is a hack
 *  */
 * fun foo() { }
 * /* STOPSHIP: */
 * </noncompliant>
 */
@ActiveByDefault(since = "1.0.0")
class ForbiddenComment(config: Config) : Rule(
    config,
    "Flags a forbidden comment."
) {
    @Configuration("forbidden comment string patterns")
    private val comments: List<Comment> by config(
        valuesWithReason(
            "FIXME:" to "Forbidden FIXME todo marker in comment, please fix the problem.",
            "STOPSHIP:" to "Forbidden STOPSHIP todo marker in comment, " +
                "please address the problem before shipping the code.",
            "TODO:" to "Forbidden TODO todo marker in comment, please do the changes.",
        )
    ) { list ->
        list.map { Comment(it.value.toRegex(setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE)), it.reason) }
    }

    @Configuration("ignores comments which match the specified regular expression. For example `Ticket|Task`.")
    private val allowedPatterns: Regex by config("", String::toRegex)

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

        comments.forEach {
            if (it.value.containsMatchIn(text)) {
                reportIssue(comment, getErrorMessage(it))
            }
        }
    }

    private fun reportIssue(comment: PsiElement, msg: String) {
        report(
            CodeSmell(
                Entity.from(comment),
                msg
            )
        )
    }

    private fun PsiComment.getContent(): String = text.getCommentContent()

    private fun getErrorMessage(comment: Comment): String =
        comment.reason ?: String.format(Locale.ROOT, DEFAULT_ERROR_MESSAGE, comment.value.pattern)

    private data class Comment(val value: Regex, val reason: String?)

    companion object {
        const val DEFAULT_ERROR_MESSAGE = "This comment contains '%s' that has been defined as forbidden."
    }
}

internal fun String.getCommentContent(): String =
    if (this.startsWith("//")) {
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

private fun String.trimIndentIgnoringFirstLine(): String =
    if ('\n' !in this) {
        this
    } else {
        val lines = this.lineSequence()
        lines.first() + "\n" + lines.drop(1).joinToString("\n").trimIndent()
    }

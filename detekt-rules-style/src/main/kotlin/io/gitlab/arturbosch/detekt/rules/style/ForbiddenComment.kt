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
    private val comments: List<Regex> by config(listOf("FIXME:", "STOPSHIP:", "TODO:")) {
        it.map(String::toRegex)
    }

    @Configuration("ignores comments which match the specified regular expression. For example `Ticket|Task`.")
    private val allowedPatterns: Regex by config("", String::toRegex)

    @Configuration("error message which overrides the default one")
    private val customMessage: String by config("")

    override fun visitComment(comment: PsiComment) {
        super.visitComment(comment)
        val text = comment.text
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
            if (it.containsMatchIn(text)) {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(comment),
                        getErrorMessage(it.pattern)
                    )
                )
            }
        }
    }

    private fun getErrorMessage(value: String): String =
        customMessage.takeUnless { it.isEmpty() } ?: String.format(DEFAULT_ERROR_MESSAGE, value)

    companion object {
        const val DEFAULT_ERROR_MESSAGE = "This comment contains '%s' " +
            "that has been defined as forbidden in detekt."
    }
}

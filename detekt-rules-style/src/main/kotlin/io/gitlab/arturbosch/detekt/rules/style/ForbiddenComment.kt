package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.valueOrDefaultCommaSeparated
import org.jetbrains.kotlin.com.intellij.psi.PsiComment

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
 *
 * @configuration values - forbidden comment strings (default: `['TODO:', 'FIXME:', 'STOPSHIP:']`)
 * @configuration allowedPatterns - ignores comments which match the specified regular expression.
 * For example `Ticket|Task`. (default: `''`)
 * @active since v1.0.0
 */
class ForbiddenComment(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName,
            Severity.Style,
            "Flags a forbidden comment. Defaults values are TODO:, FIXME: or STOPSHIP:",
            Debt.TEN_MINS)

    private val values: List<String> = valueOrDefaultCommaSeparated(VALUES, listOf("TODO:", "FIXME:", "STOPSHIP:"))

    private val allowedPatterns: Regex = Regex(valueOrDefault(ALLOWED_PATTERNS, ""))

    override fun visitComment(comment: PsiComment) {
        super.visitComment(comment)

        val text = comment.text

        if (allowedPatterns.pattern.isNotEmpty() && allowedPatterns.containsMatchIn(text)) return

        values.forEach {
            if (text.contains(it, ignoreCase = true)) {
                report(CodeSmell(issue, Entity.from(comment), "This comment contains text that has been " +
                        "defined as forbidden in detekt."))
            }
        }
    }

    companion object {
        const val VALUES = "values"
        const val ALLOWED_PATTERNS = "allowedPatterns"
    }
}

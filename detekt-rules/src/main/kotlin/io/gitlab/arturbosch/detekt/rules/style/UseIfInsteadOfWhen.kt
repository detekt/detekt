package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * Binary expressions are better expressed using an `if` expression than a `when` expression.
 *
 * See https://kotlinlang.org/docs/reference/coding-conventions.html#if-versus-when
 *
 * <noncompliant>
 * when (x) {
 *   null -> true
 *   else -> false
 * }
 * </noncompliant>
 *
 * <compliant>
 * if (x == null) true else false
 * </compliant>
 */
class UseIfInsteadOfWhen(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue("UseIfInsteadOfWhen",
            Severity.Style,
            "Binary expressions are better expressed using an 'if' expression than a 'when' expression.",
            Debt.FIVE_MINS)

    override fun visitWhenExpression(expression: KtWhenExpression) {
        super.visitWhenExpression(expression)

        if (expression.entries.size == 2 &&
            expression.elseExpression != null &&
            expression.entries.none { it.conditions.size > 1 }) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(expression),
                    "Prefer using 'if' for binary conditions instead of 'when'."
                )
            )
        }
    }
}

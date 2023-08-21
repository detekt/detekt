package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * Binary expressions are better expressed using an `if` expression than a `when` expression.
 *
 * See [if versus when](https://kotlinlang.org/docs/coding-conventions.html#if-versus-when)
 *
 * <noncompliant>
 * when (x) {
 *     null -> true
 *     else -> false
 * }
 * </noncompliant>
 *
 * <compliant>
 * if (x == null) true else false
 * </compliant>
 */
class UseIfInsteadOfWhen(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(
        "UseIfInsteadOfWhen",
        "Binary expressions are better expressed using an `if` expression than a `when` expression.",
        Debt.FIVE_MINS
    )

    @Configuration("ignores when statements with a variable declaration used in the subject")
    private val ignoreWhenContainingVariableDeclaration: Boolean by config(false)

    override fun visitWhenExpression(expression: KtWhenExpression) {
        super.visitWhenExpression(expression)

        if (ignoreWhenContainingVariableDeclaration && expression.subjectExpression is KtProperty) return

        if (expression.entries.size == 2 &&
            expression.elseExpression != null &&
            expression.entries.none { it.conditions.size > 1 }
        ) {
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

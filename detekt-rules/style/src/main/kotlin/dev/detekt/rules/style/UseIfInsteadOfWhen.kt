package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
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
class UseIfInsteadOfWhen(config: Config) :
    Rule(
        config,
        "Binary expressions are better expressed using an `if` expression than a `when` expression."
    ) {

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
                Finding(
                    Entity.from(expression),
                    "Prefer using 'if' for binary conditions instead of 'when'."
                )
            )
        }
    }
}

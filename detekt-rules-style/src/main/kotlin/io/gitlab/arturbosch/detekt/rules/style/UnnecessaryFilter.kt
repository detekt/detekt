package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * Unnecessary filter add complexity to the code and accomplish noting. They should be removed.
 *
 *
 * <noncompliant>
 * val x = listOf(1, 2, 3)
 *      .filter { it > 1 }
 *      .count()
 *
 * val x = listOf(1, 2, 3)
 *      .filter { it > 1 }
 *      .isEmpty()
 * </noncompliant>
 *
 * <compliant>
 * val x = listOf(1, 2, 3)
 *      .count { it > 2 }
 * }
 *
 * val x = listOf(1, 2, 3)
 *      .none { it > 1 }
 */
class UnnecessaryFilter(config: Config = Config.empty) : Rule(config) {
    override val issue: Issue = Issue("UnnecessaryFilter", Severity.Style,
        "UnnecessaryFilter",
        Debt.FIVE_MINS)

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (bindingContext == BindingContext.EMPTY) return

        val calleeExpression = expression.calleeExpression
        if (calleeExpression?.text != "filter") return
    }
}

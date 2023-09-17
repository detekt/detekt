package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression

/**
 * Detects `if` statements that could be expressions.
 *
 * This is possible under the following two conditions:
 *
 * 1. The last statement of every branch is a `return`.
 * 2. The last statement of every branch is an assignment to the same variable with the same operator.
 *
 * Refactoring the statements to expressions reduces the `return` and assignment count. This improves readability.
 *
 * <noncompliant>
 *     if (foo == 0) {
 *       return 0
 *     } else {
 *       return 1
 *     }
 *
 *     if (foo == 0) {
 *       foo = 1
 *     } else {
 *       foo = -1
 *     }
 * </noncompliant>
 *
 * <compliant>
 *     return if (foo == 0) {
 *       0
 *     } else {
 *       1
 *     }
 *
 *     foo = if (foo == 0) {
 *       1
 *     } else {
 *       -1
 *     }
 * </compliant>
 */

class IfStatementCouldBeExpression(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        "IfStatementCouldBeExpression",
        "if statement could be expression. Use an if expression to reduce the return and assignment count.",
        Debt.FIVE_MINS,
    )

    override fun visitIfExpression(expression: KtIfExpression) {
        if (canLiftOutOf(expression)) {
            report(CodeSmell(issue, Entity.from(expression), issue.description))
        }
    }

    private fun canLiftOutOf(expression: KtIfExpression): Boolean {
        val branches = getBranches(expression)
        return isExhaustive(expression) && canLiftOut(branches)
    }

    private fun getBranches(expression: KtIfExpression): List<KtExpression> {
        val list = mutableListOf<KtExpression>()
        var current: KtExpression? = expression
        while (current is KtIfExpression) {
            current.then?.let { list.add(it) }
            // Don't add `if` because it's an `else if` which we treat as one unit.
            current.`else`?.takeIf { it !is KtIfExpression }?.let { list.add(it) }
            current = current.`else`
        }
        return list
    }

    private fun isExhaustive(expression: KtIfExpression): Boolean {
        return expression.`else` != null
    }
}

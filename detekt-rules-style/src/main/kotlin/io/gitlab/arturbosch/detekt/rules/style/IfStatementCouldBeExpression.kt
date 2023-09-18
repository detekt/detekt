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
 * An `if` statement can be an expression if it has the following properties:
 *
 * 1. The `if` statement is exhaustive i.e. it has an `else` branch.
 * 2. The last statement of every branch is either a `return` or an assignment to the same variable with the same operator.
 *
 * Refactoring the statements to expressions reduces the `return` and assignment count. This improves readability.
 *
 * <noncompliant>
 *     if (a == 1) {
 *       a = 1
 *     } else {
 *       a = 0
 *     }
 *
 *     if (a == 1) {
 *       return 1
 *     } else {
 *       return 0
 *     }
 * </noncompliant>
 *
 * <compliant>
 *     a = if (a == 1) {
 *       1
 *     } else {
 *       0
 *     }
 *
 *     return if (a == 1) {
 *       1
 *     } else {
 *       0
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
        val branches = mutableListOf<KtExpression>()
        var current: KtExpression? = expression
        while (current is KtIfExpression) {
            current.then?.let { branches.add(it) }
            // Don't add `if` because it's an `else if` which we treat as one unit.
            current.`else`?.takeIf { it !is KtIfExpression }?.let { branches.add(it) }
            current = current.`else`
        }
        return branches
    }

    private fun isExhaustive(expression: KtIfExpression): Boolean {
        var hasElse = false
        var current: KtExpression? = expression
        while (current is KtIfExpression && !hasElse) {
            hasElse = current.`else` != null && current.`else` !is KtIfExpression
            current = current.`else`
        }
        return hasElse
    }
}

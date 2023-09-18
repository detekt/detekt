package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.cfg.WhenChecker
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * Detects `when` statements that could be expressions.
 *
 * A `when` statement can be an expression if it has the following properties:
 *
 * 1. The `when` statement is exhaustive i.e. it has an `else` branch or the branch conditions cover all possible cases.
 * 2. The last statement of every branch is either a `return` or an assignment to the same variable with the same operator.
 *
 * Refactoring the statements to expressions reduces the `return` and assignment count. This improves readability.
 *
 * <noncompliant>
 *     when (a) {
 *       1 -> a = 1
 *       else -> a = 0
 *     }
 *
 *     when (a) {
 *       1 -> return 1
 *       else -> return 0
 *     }
 * </noncompliant>
 *
 * <compliant>
 *     a = when (a) {
 *       1 -> 1
 *       else -> 0
 *     }
 *
 *     return when (a) {
 *       1 -> 1
 *       else -> 0
 *     }
 * </compliant>
 */

@RequiresTypeResolution
class WhenStatementCouldBeExpression(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        "WhenStatementCouldBeExpression",
        "when statement could be expression. Use a when expression to reduce the return and assignment count.",
        Debt.FIVE_MINS,
    )

    override fun visitWhenExpression(expression: KtWhenExpression) {
        if (canLiftOutOf(expression)) {
            report(CodeSmell(issue, Entity.from(expression), issue.description))
        }
    }

    private fun canLiftOutOf(expression: KtWhenExpression): Boolean {
        return isExhaustive(expression) && canLiftOut(expression.entries)
    }

    private fun isExhaustive(expression: KtWhenExpression): Boolean {
        val missingCases = WhenChecker.getMissingCases(expression, bindingContext)
        return missingCases.isEmpty() || expression.elseExpression != null
    }
}

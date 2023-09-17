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
 * This is possible under the following two conditions:
 *
 * 1. The last statement of every entry is a `return`.
 * 2. The last statement of every entry is an assignment to the same variable with the same operator.
 *
 * Refactoring the statements to expressions reduces the `return` and assignment count. This improves readability.
 *
 * <noncompliant>
 *     when (foo > 0) {
 *       true -> baz = 0
 *       false -> baz = 1
 *     }
 *
 *     when (foo > 0) {
 *       true -> return 0
 *       false -> return 1
 *     }
 * </noncompliant>
 *
 * <compliant>
 *     baz = when (foo > 0) {
 *       true -> 0
 *       false -> 1
 *     }
 *
 *     return when (foo > 0) {
 *       true -> 0
 *       false -> 1
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
        return missingCases.isEmpty()
    }
}

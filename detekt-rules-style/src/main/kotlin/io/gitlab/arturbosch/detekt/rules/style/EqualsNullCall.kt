package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression

/**
 * To compare an object with `null` prefer using `==`. This rule detects and reports instances in the code where the
 * `equals()` method is used to compare a value with `null`.
 *
 * <noncompliant>
 * fun isNull(str: String) = str.equals(null)
 * </noncompliant>
 *
 * <compliant>
 * fun isNull(str: String) = str == null
 * </compliant>
 *
 * @active since v1.2.0
 */
class EqualsNullCall(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue("EqualsNullCall", Severity.Style,
            "Equals() method is called with null as parameter. Consider using == to compare to null.",
            Debt.FIVE_MINS)

    override fun visitCallExpression(expression: KtCallExpression) {
        if (expression.calleeExpression?.text == "equals" && hasNullParameter(expression)) {
            report(CodeSmell(issue, Entity.from(expression), issue.description))
        } else {
            super.visitCallExpression(expression)
        }
    }

    private fun hasNullParameter(expression: KtCallExpression) =
        expression.valueArguments.singleOrNull()?.text == "null"
}

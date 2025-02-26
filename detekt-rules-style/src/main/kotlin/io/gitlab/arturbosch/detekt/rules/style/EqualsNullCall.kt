package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
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
 */
@ActiveByDefault(since = "1.2.0")
class EqualsNullCall(config: Config) : Rule(
    config,
    "Equals() method is called with null as parameter. Consider using == to compare to null."
) {

    override fun visitCallExpression(expression: KtCallExpression) {
        if (expression.calleeExpression?.text == "equals" && hasNullParameter(expression)) {
            report(Finding(Entity.from(expression), description))
        } else {
            super.visitCallExpression(expression)
        }
    }

    private fun hasNullParameter(expression: KtCallExpression) =
        expression.valueArguments.singleOrNull()?.text == "null"
}

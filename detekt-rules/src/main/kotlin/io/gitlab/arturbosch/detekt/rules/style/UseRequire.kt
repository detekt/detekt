package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType

/**
 * Kotlin provides a much more concise way to check preconditions than to manually throw an
 * IllegalArgumentException.
 *
 * <noncompliant>
 * if (value == null) throw new IllegalArgumentException("value should not be null")
 * if (value < 0) throw new IllegalArgumentException("value is $value but should be at least 0")
 * </noncompliant>
 *
 * <compliant>
 * requireNotNull(value) {"value should not be null"}
 * require(value >= 0) { "value is $value but should be at least 0" }
 * </compliant>
 *
 * @author Markus Schwarz
 */
class UseRequire(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        "UseRequire", Severity.Style,
        "Use require() instead of throwing an IllegalArgumentException.",
        Debt.FIVE_MINS
    )

    override fun visitThrowExpression(expression: KtThrowExpression) {
        if (expression.isIllegalArgumentException() && !expression.isOnlyExpressionInBlock()) {
            report(CodeSmell(issue, Entity.from(expression), issue.description))
        }
    }

    private fun KtThrowExpression.isIllegalArgumentException(): Boolean {
        val callExpression = findDescendantOfType<KtCallExpression>()
        val argumentCount = callExpression?.valueArgumentList?.children?.size ?: 0

        return callExpression?.firstChild?.text == "IllegalArgumentException" && argumentCount < 2
    }

    private fun KtThrowExpression.isOnlyExpressionInBlock(): Boolean {
        return when (val p = parent) {
            is KtBlockExpression -> p.statements.size == 1
            is KtNamedFunction -> true
            else -> false
        }
    }
}

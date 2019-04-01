package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType

/**
 * Kotlin provides a much more concise way to check invariants as well as pre- and post conditions than to manually throw
 * an IllegalStateException.
 *
 * <noncompliant>
 * if (value == null) throw new IllegalStateException("value should not be null")
 * if (value < 0) throw new IllegalStateException("value is $value but should be at least 0")
 * when(a) {
 *   1 -> doSomething()
 *   else -> throw IllegalStateException("Unexpected value")
 * }
 * </noncompliant>
 *
 * <compliant>
 * checkNotNull(value) {"value should not be null"}
 * check(value >= 0) { "value is $value but should be at least 0" }
 * when(a) {
 *   1 -> doSomething()
 *   else -> error("Unexpected value")
 * }
 * </compliant>
 *
 * @author Markus Schwarz
 */
class UseCheckOrError(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        "UseRequire", Severity.Style,
        "Use check() or error() instead of throwing an IllegalStateException.",
        Debt.FIVE_MINS
    )

    override fun visitThrowExpression(expression: KtThrowExpression) {
        if (expression.isIllegalStateExceptionWithoutCause()) {
            report(CodeSmell(issue, Entity.from(expression), issue.description))
        }
    }

    private fun KtThrowExpression.isIllegalStateExceptionWithoutCause(): Boolean {
        val callExpression = findDescendantOfType<KtCallExpression>()
        val argumentCount = callExpression?.valueArgumentList?.children?.size ?: 0

        return callExpression?.firstChild?.text == "IllegalStateException" && argumentCount < 2
    }
}

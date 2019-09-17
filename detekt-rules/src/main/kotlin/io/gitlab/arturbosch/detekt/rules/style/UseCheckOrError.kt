package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.argumentCount
import io.gitlab.arturbosch.detekt.rules.isIllegalStateException
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtThrowExpression

/**
 * Kotlin provides a much more concise way to check invariants as well as pre- and post conditions.
 * Prefer them instead of manually throwing an IllegalStateException.
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
 */
class UseCheckOrError(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        "UseCheckOrError", Severity.Style,
        "Use check() or error() instead of throwing an IllegalStateException.",
        Debt.FIVE_MINS
    )

    override fun visitThrowExpression(expression: KtThrowExpression) {
        if (expression.isOnlyExpressionInLambda()) return

        if (expression.isIllegalStateException() && expression.argumentCount < 2) {
            report(CodeSmell(issue, Entity.from(expression), issue.description))
        }
    }

    private fun KtThrowExpression.isOnlyExpressionInLambda(): Boolean {
        val p = parent
        return if (p is KtBlockExpression) p.statements.size == 1 && p.parent is KtFunctionLiteral else false
    }
}

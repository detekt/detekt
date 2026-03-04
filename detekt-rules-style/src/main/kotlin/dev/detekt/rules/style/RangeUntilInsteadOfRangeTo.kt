package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtExpression

/**
 * Reports calls to `..` operator instead of calls to `..<`.
 * `..<` is applicable in cases where the upper range value is described as
 * open ended range(or in case of integral types some value subtracted by 1).
 * `..<` helps to prevent off-by-one errors.
 *
 * <noncompliant>
 * for (i in 0..10 - 1) {}
 * val range = 0..10 - 1
 * </noncompliant>
 *
 * <compliant>
 * for (i in 0..<10) {}
 * val range = 0..<10
 * </compliant>
 */
class RangeUntilInsteadOfRangeTo(config: Config) : Rule(config, "A `..` call can be replaced with `..<`.") {

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        if (expression.operationReference.text == RANGE_TO_OPERATOR &&
            expression.right.isMinusOneExpression()
        ) {
            report(expression, RANGE_TO_OPERATOR)
        }
        super.visitBinaryExpression(expression)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        if (expression.calleeExpression?.text == RANGE_TO &&
            expression.valueArguments.singleOrNull()?.getArgumentExpression().isMinusOneExpression()
        ) {
            report(expression, RANGE_TO)
        }
        super.visitCallExpression(expression)
    }

    private fun KtExpression?.isMinusOneExpression() =
        this is KtBinaryExpression &&
            left != null &&
            operationToken == KtTokens.MINUS &&
            (right as? KtConstantExpression)?.text == "1"

    private fun report(expression: KtExpression, rangeTo: String) {
        report(
            Finding(
                Entity.from(expression),
                "`$rangeTo` call can be replaced with `..<`"
            )
        )
    }

    companion object {
        private const val RANGE_TO_OPERATOR = ".."
        private const val RANGE_TO = "rangeTo"
    }
}

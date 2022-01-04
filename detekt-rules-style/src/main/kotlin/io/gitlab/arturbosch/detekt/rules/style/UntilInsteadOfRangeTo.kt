package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtExpression

/**
 * Reports calls to '..' operator instead of calls to 'until'.
 * 'until' is applicable in cases where the upper range value is described as
 * some value subtracted by 1. 'until' helps to prevent off-by-one errors.
 *
 * <noncompliant>
 * for (i in 0 .. 10 - 1) {}
 * val range = 0 .. 10 - 1
 * </noncompliant>
 *
 * <compliant>
 * for (i in 0 until 10) {}
 * val range = 0 until 10
 * </compliant>
 */
class UntilInsteadOfRangeTo(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "A `..` call can be replaced with `until`.",
        Debt.FIVE_MINS
    )

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        if (expression.operationReference.text == rangeToOperator &&
            expression.right.isMinusOneExpression()
        ) {
            report(expression, rangeToOperator)
        }
        super.visitBinaryExpression(expression)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        if (expression.calleeExpression?.text == rangeTo &&
            expression.valueArguments.singleOrNull()?.getArgumentExpression().isMinusOneExpression()
        ) {
            report(expression, rangeTo)
        }
        super.visitCallExpression(expression)
    }

    private fun KtExpression?.isMinusOneExpression() = this is KtBinaryExpression &&
        left != null && operationToken == KtTokens.MINUS && (right as? KtConstantExpression)?.text == "1"

    private fun report(expression: KtExpression, rangeTo: String) {
        report(CodeSmell(issue, Entity.from(expression), "'$rangeTo' call can be replaced with 'until'"))
    }

    companion object {
        private const val rangeToOperator = ".."
        private const val rangeTo = "rangeTo"
    }
}

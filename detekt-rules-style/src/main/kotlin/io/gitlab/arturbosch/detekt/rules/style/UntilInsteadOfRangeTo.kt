package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression

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

    override val issue = Issue(javaClass.simpleName,
            Severity.Style,
            "'..' call can be replaced with 'until'",
            Debt.FIVE_MINS)

    private val minimumSize = 3

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        if (isUntilApplicable(expression.children)) {
            report(CodeSmell(issue, Entity.from(expression), "'..' call can be replaced with 'until'"))
        }
        super.visitBinaryExpression(expression)
    }

    private fun isUntilApplicable(range: Array<PsiElement>): Boolean {
        if (range.size >= minimumSize && range[1] is KtOperationReferenceExpression && range[1].text == "..") {
            val expression = range[2] as? KtBinaryExpression
            if (expression?.operationToken == KtTokens.MINUS) {
                val rightExpressionValue = expression?.right as? KtConstantExpression
                return rightExpressionValue?.text == "1"
            }
        }
        return false
    }
}

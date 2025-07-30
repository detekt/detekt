package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.psi.isCalling
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression

/**
 * Detects expressions with two or more calls of operator `not` could be simplified.
 *
 * <noncompliant>
 * isValid.not().not()
 * !isValid.not()
 * !!isValid
 * </noncompliant>
 *
 * <compliant>
 * isValid
 * </compliant>
 */
class DoubleNegativeExpression(config: Config) :
    Rule(
        config,
        "Expression with two or more calls of operator `not` could be simplified.",
    ),
    RequiresAnalysisApi {

    override fun visitPrefixExpression(expression: KtPrefixExpression) {
        super.visitPrefixExpression(expression)
        check(expression)
    }

    override fun visitQualifiedExpression(expression: KtQualifiedExpression) {
        super.visitQualifiedExpression(expression)
        check(expression)
    }

    private fun check(expression: KtExpression) {
        val parent = expression.parent
        if (parent is KtPrefixExpression || parent is KtQualifiedExpression) return
        if (expression.isDoubleNegativeExpression()) {
            val finding = Finding(
                Entity.from(expression),
                "Expression with two or more calls of operator `not` could be simplified.",
            )
            report(finding)
        }
    }

    private fun KtExpression.isDoubleNegativeExpression(): Boolean {
        var expr: KtExpression? = this
        var count = 0
        while (count < 2) {
            when {
                expr is KtPrefixExpression && expr.operationToken == KtTokens.EXCL -> {
                    count++
                    expr = expr.baseExpression
                }

                expr is KtQualifiedExpression && expr.selectorExpression.isBooleanNotCall() -> {
                    count++
                    expr = expr.receiverExpression
                }

                expr is KtCallExpression && expr.isBooleanNotCall() -> {
                    count++
                    expr = null
                }

                else -> break
            }
        }
        return count == 2
    }

    private fun KtExpression?.isBooleanNotCall(): Boolean = this is KtCallExpression && isCalling(booleanNotCallableId)

    companion object {
        private val booleanNotCallableId = CallableId(StandardClassIds.Boolean, Name.identifier("not"))
    }
}

package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

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
    RequiresTypeResolution {
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
            val codeSmell = CodeSmell(
                Entity.from(expression),
                "Expression with two or more calls of operator `not` could be simplified.",
            )
            report(codeSmell)
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

    private fun KtExpression?.isBooleanNotCall(): Boolean =
        this is KtCallExpression && getResolvedCall(bindingContext)?.resultingDescriptor?.fqNameSafe == booleanNotFqName

    companion object {
        private val booleanNotFqName = FqName("kotlin.Boolean.not")
    }
}

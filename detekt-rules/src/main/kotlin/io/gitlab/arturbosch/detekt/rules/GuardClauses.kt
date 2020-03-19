package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType

fun KtNamedFunction.yieldStatementsSkippingGuardClauses(): Sequence<KtExpression> = sequence {
    var firstNonGuardFound = false
    this@yieldStatementsSkippingGuardClauses.bodyBlockExpression?.statements?.forEach {
        if (firstNonGuardFound) {
            yield(it)
        } else {
            if (!it.isGuardClause()) {
                firstNonGuardFound = true
                yield(it)
            }
        }
    }
}

fun KtExpression.isGuardClause(): Boolean {

    fun KtReturnExpression.isIfConditionGuardClause(): Boolean {
        val ifExpr = this.parent?.parent as? KtIfExpression
        return ifExpr != null && ifExpr.`else` == null
    }

    fun KtReturnExpression.isElvisOperatorGuardClause(): Boolean {
        val elvisExpr = this.parent as? KtBinaryExpression
        return elvisExpr != null && elvisExpr.operationToken == KtTokens.ELVIS
    }

    val returnExpr = this.findDescendantOfType<KtReturnExpression>() ?: return false
    return returnExpr.isIfConditionGuardClause() || returnExpr.isElvisOperatorGuardClause()
}

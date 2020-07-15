package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.lastBlockStatementOrThis

internal inline fun <reified T:KtExpression> KtNamedFunction.yieldStatementsSkippingGuardClauses(): Sequence<KtExpression> = sequence {
    var firstNonGuardFound = false
    this@yieldStatementsSkippingGuardClauses.bodyBlockExpression?.statements?.forEach {
        if (firstNonGuardFound) {
            yield(it)
        } else {
            if (!it.isGuardClause<T>()) {
                firstNonGuardFound = true
                yield(it)
            }
        }
    }
}

internal inline fun <reified T:KtExpression> KtExpression.isGuardClause(): Boolean {
    val descendantExpr = this.findDescendantOfType<T>() ?: return false
    return this.isIfConditionGuardClause(descendantExpr) || this.isElvisOperatorGuardClause()
}

internal fun <T:KtExpression> KtExpression.isIfConditionGuardClause(descendantExpr: T): Boolean {
    val ifExpr = this as? KtIfExpression ?: return false
    return ifExpr.`else` == null &&
            descendantExpr === ifExpr.then?.lastBlockStatementOrThis()
}

internal fun KtExpression.isElvisOperatorGuardClause(): Boolean {
    val elvisExpr = this.findDescendantOfType<KtBinaryExpression>() ?: return false
    return elvisExpr.operationToken == KtTokens.ELVIS
}

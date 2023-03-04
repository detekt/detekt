package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtSuperExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.lastBlockStatementOrThis

inline fun <reified T : KtExpression> KtNamedFunction.yieldStatementsSkippingGuardClauses(): Sequence<KtExpression> =
    sequence {
        var firstNonGuardFound = false
        this@yieldStatementsSkippingGuardClauses.bodyBlockExpression?.statements?.forEach {
            if (firstNonGuardFound) {
                yield(it)
            } else if (!it.isGuardClause<T>() && !it.isSuperCall()) {
                firstNonGuardFound = true
                yield(it)
            }
        }
    }

fun KtExpression.isSuperCall(): Boolean {
    return (this as? KtDotQualifiedExpression)?.receiverExpression is KtSuperExpression
}

inline fun <reified T : KtExpression> KtExpression.isGuardClause(): Boolean {
    val descendantExpr = this.findDescendantOfType<T>() ?: return false
    return this.isIfConditionGuardClause(descendantExpr) || this.isElvisOperatorGuardClause(descendantExpr)
}

fun <T : KtExpression> KtExpression.isIfConditionGuardClause(descendantExpr: T): Boolean {
    val ifExpr = this as? KtIfExpression ?: return false
    return ifExpr.`else` == null &&
        descendantExpr === ifExpr.then?.lastBlockStatementOrThis()
}

fun <T : KtExpression> KtExpression.isElvisOperatorGuardClause(descendantExpr: T): Boolean =
    this.anyDescendantOfType<KtBinaryExpression> { it.operationToken == KtTokens.ELVIS && it.right == descendantExpr }

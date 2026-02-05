package dev.detekt.rules.style

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtSuperExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.elementsInRange
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import org.jetbrains.kotlin.psi.psiUtil.lastBlockStatementOrThis

/**
 * Util function to search for the [KtElement]s in the parents of
 * the given [line] from a given offset in a [KtFile].
 */
internal fun findKtElementInParents(file: KtFile, offset: Int, line: String): Sequence<PsiElement> =
    file.elementsInRange(TextRange.create(offset, offset + line.length))
        .asSequence()
        .plus(file.findElementAt(offset))
        .mapNotNull { it?.getNonStrictParentOfType() }

inline fun <reified T : KtExpression> KtNamedFunction.yieldStatementsSkippingGuardClauses(): Sequence<KtExpression> =
    sequence {
        var firstNonGuardFound = false
        this@yieldStatementsSkippingGuardClauses.bodyBlockExpression?.statements?.forEach {
            if (firstNonGuardFound) {
                yield(it)
            } else if (!it.isGuardClause<T>() && !it.isSuperCall() && !it.isScopedAssignment()) {
                firstNonGuardFound = true
                yield(it)
            }
        }
    }

fun KtExpression.isSuperCall(): Boolean = (this as? KtDotQualifiedExpression)?.receiverExpression is KtSuperExpression

fun KtExpression.isScopedAssignment(): Boolean =
    this is KtProperty && !isVar && (initializer as? KtNameReferenceExpression)?.getReferencedName() == name

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

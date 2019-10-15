package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtLoopExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectLiteralExpression
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

/**
 * Counts the cyclomatic complexity of functions.
 */
class McCabeVisitor(private val ignoreSimpleWhenEntries: Boolean) : DetektVisitor() {

    var mcc: Int = 0
        private set

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (!isInsideObjectLiteral(function)) {
            mcc++
            super.visitNamedFunction(function)
        }
    }

    private fun isInsideObjectLiteral(function: KtNamedFunction): Boolean =
            function.getStrictParentOfType<KtObjectLiteralExpression>() != null

    override fun visitIfExpression(expression: KtIfExpression) {
        mcc++
        val condition = expression.condition
        if (condition != null) {
            mcc += condition
                .collectDescendantsOfType<KtOperationReferenceExpression>()
                .count { it.operationSignTokenType == KtTokens.ANDAND || it.operationSignTokenType == KtTokens.OROR }
        }
        super.visitIfExpression(expression)
    }

    override fun visitLoopExpression(loopExpression: KtLoopExpression) {
        mcc++
        super.visitLoopExpression(loopExpression)
    }

    override fun visitWhenExpression(expression: KtWhenExpression) {
        val entries = expression.extractEntries(ignoreSimpleWhenEntries)
        mcc += if (ignoreSimpleWhenEntries && entries.count() == 0) 1 else entries.count()
        super.visitWhenExpression(expression)
    }

    private fun KtWhenExpression.extractEntries(ignoreSimpleWhenEntries: Boolean): Sequence<KtWhenEntry> {
        val entries = entries.asSequence()
        return if (ignoreSimpleWhenEntries) entries.filter { it.expression is KtBlockExpression } else entries
    }

    override fun visitTryExpression(expression: KtTryExpression) {
        mcc += expression.catchClauses.size
        super.visitTryExpression(expression)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        if (expression.isUsedForNesting()) {
            val lambdaArguments = expression.lambdaArguments
            if (lambdaArguments.size > 0) {
                val lambdaArgument = lambdaArguments[0]
                lambdaArgument.getLambdaExpression()?.bodyExpression?.let {
                    mcc++
                    Unit
                }
            }
        }
        super.visitCallExpression(expression)
    }
}

fun KtCallExpression.isUsedForNesting(): Boolean = when (getCallNameExpression()?.text) {
    "run", "let", "apply", "with", "also", "use", "forEach", "isNotNull", "ifNull" -> true
    else -> false
}

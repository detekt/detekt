package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtElement
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
class CyclomaticComplexity(private val ignoreSimpleWhenEntries: Boolean) : DetektVisitor() {

    data class Config(var ignoreSimpleWhenEntries: Boolean = false)

    companion object {

        fun calculate(node: KtElement, configure: (Config.() -> Unit)? = null): Int {
            val config = Config()
            configure?.invoke(config)
            val visitor = CyclomaticComplexity(config.ignoreSimpleWhenEntries)
            node.accept(visitor)
            return visitor.complexity
        }
    }

    var complexity: Int = 0
        private set

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (!isInsideObjectLiteral(function)) {
            complexity++
            super.visitNamedFunction(function)
        }
    }

    private fun isInsideObjectLiteral(function: KtNamedFunction): Boolean =
        function.getStrictParentOfType<KtObjectLiteralExpression>() != null

    override fun visitIfExpression(expression: KtIfExpression) {
        complexity++
        val condition = expression.condition
        if (condition != null) {
            complexity += condition
                .collectDescendantsOfType<KtOperationReferenceExpression>()
                .count { it.operationSignTokenType == KtTokens.ANDAND || it.operationSignTokenType == KtTokens.OROR }
        }
        super.visitIfExpression(expression)
    }

    override fun visitLoopExpression(loopExpression: KtLoopExpression) {
        complexity++
        super.visitLoopExpression(loopExpression)
    }

    override fun visitWhenExpression(expression: KtWhenExpression) {
        val entries = expression.extractEntries(ignoreSimpleWhenEntries)
        complexity += if (ignoreSimpleWhenEntries && entries.count() == 0) 1 else entries.count()
        super.visitWhenExpression(expression)
    }

    private fun KtWhenExpression.extractEntries(ignoreSimpleWhenEntries: Boolean): Sequence<KtWhenEntry> {
        val entries = entries.asSequence()
        return if (ignoreSimpleWhenEntries) entries.filter { it.expression is KtBlockExpression } else entries
    }

    override fun visitTryExpression(expression: KtTryExpression) {
        complexity += expression.catchClauses.size
        super.visitTryExpression(expression)
    }

    private fun KtCallExpression.isUsedForNesting(): Boolean = when (getCallNameExpression()?.text) {
        "run", "let", "apply", "with", "also", "use", "forEach", "isNotNull", "ifNull" -> true
        else -> false
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        if (expression.isUsedForNesting()) {
            val lambdaArguments = expression.lambdaArguments
            if (lambdaArguments.size > 0) {
                val lambdaArgument = lambdaArguments[0]
                lambdaArgument.getLambdaExpression()?.bodyExpression?.let {
                    complexity++
                    Unit
                }
            }
        }
        super.visitCallExpression(expression)
    }
}

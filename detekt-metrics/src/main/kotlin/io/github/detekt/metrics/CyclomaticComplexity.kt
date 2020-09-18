package io.github.detekt.metrics

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtBreakExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtContinueExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtLoopExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectLiteralExpression
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

/**
 * Counts the cyclomatic complexity of nodes.
 */
@Suppress("TooManyFunctions")
class CyclomaticComplexity(private val config: Config) : DetektVisitor() {

    class Config(
        var ignoreSimpleWhenEntries: Boolean = false,
        var ignoreNestingFunctions: Boolean = false,
        var nestingFunctions: Set<String> = DEFAULT_NESTING_FUNCTIONS
    )

    var complexity: Int = 0
        private set

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (!isInsideObjectLiteral(function)) {
            complexity++
            super.visitNamedFunction(function)
        }
    }

    private fun isInsideObjectLiteral(function: KtNamedFunction) =
        function.getStrictParentOfType<KtObjectLiteralExpression>() != null

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        if (expression.operationToken in CONDITIONALS) {
            complexity++
        }
        super.visitBinaryExpression(expression)
    }

    override fun visitContinueExpression(expression: KtContinueExpression) {
        complexity++
        super.visitContinueExpression(expression)
    }

    override fun visitBreakExpression(expression: KtBreakExpression) {
        complexity++
        super.visitBreakExpression(expression)
    }

    override fun visitIfExpression(expression: KtIfExpression) {
        complexity++
        super.visitIfExpression(expression)
    }

    override fun visitLoopExpression(loopExpression: KtLoopExpression) {
        complexity++
        super.visitLoopExpression(loopExpression)
    }

    override fun visitWhenExpression(expression: KtWhenExpression) {
        val entries = expression.extractEntries()
        complexity += if (config.ignoreSimpleWhenEntries && entries.count() == 0) 1 else entries.count()
        super.visitWhenExpression(expression)
    }

    private fun KtWhenExpression.extractEntries(): Sequence<KtWhenEntry> {
        val entries = entries.asSequence()
        return if (config.ignoreSimpleWhenEntries) entries.filter { it.expression is KtBlockExpression } else entries
    }

    override fun visitTryExpression(expression: KtTryExpression) {
        complexity += expression.catchClauses.size
        super.visitTryExpression(expression)
    }

    private fun KtCallExpression.isUsedForNesting(): Boolean = when (getCallNameExpression()?.text) {
        in config.nestingFunctions -> true
        else -> false
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        if (!config.ignoreNestingFunctions && expression.isUsedForNesting()) {
            val lambdaExpression = expression.lambdaArguments.firstOrNull()?.getLambdaExpression()
            lambdaExpression?.bodyExpression?.let {
                complexity++
            }
        }
        super.visitCallExpression(expression)
    }

    companion object {

        val CONDITIONALS = setOf(KtTokens.ELVIS, KtTokens.ANDAND, KtTokens.OROR)
        val DEFAULT_NESTING_FUNCTIONS = setOf(
            "run",
            "let",
            "apply",
            "with",
            "also",
            "use",
            "forEach",
            "isNotNull",
            "ifNull"
        )

        fun calculate(node: KtElement, configure: (Config.() -> Unit)? = null): Int {
            val config = Config()
            configure?.invoke(config)
            val visitor = CyclomaticComplexity(config)
            node.accept(visitor)
            return visitor.complexity
        }
    }
}

package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtContainerNodeForControlStructureBody
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtLoopExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression

/**
 * This rule reports excessive nesting depth in functions. Excessively nested code becomes harder to read and increases
 * its hidden complexity. It might become harder to understand edge-cases of the function.
 *
 * Prefer extracting the nested code into well-named functions to make it easier to understand.
 */
@ActiveByDefault(since = "1.0.0")
class NestedBlockDepth(config: Config) : Rule(
    config,
    "Excessive nesting leads to hidden complexity. Prefer extracting code to make it easier to understand."
) {

    @Configuration("The maximum allowed nested block depth for a function")
    private val allowedDepth: Int by config(defaultValue = 4)

    override fun visitNamedFunction(function: KtNamedFunction) {
        val visitor = FunctionDepthVisitor(allowedDepth)
        visitor.visitNamedFunction(function)
        if (visitor.isTooDeep) {
            @Suppress("UnsafeCallOnNullableType")
            report(
                CodeSmell(
                    Entity.atName(function),
                    "Function ${function.name} is nested too deeply."
                )
            )
        }
    }

    private class FunctionDepthVisitor(val allowedDepth: Int) : DetektVisitor() {

        var depth = 0
        var maxDepth = 0
        var isTooDeep = false
        private fun inc() {
            depth++
            if (depth > allowedDepth) {
                isTooDeep = true
                if (depth > maxDepth) maxDepth = depth
            }
        }

        private fun dec() {
            depth--
        }

        override fun visitIfExpression(expression: KtIfExpression) {
            // Prevents else if (...) to count as two - #51
            if (expression.parent !is KtContainerNodeForControlStructureBody) {
                inc()
                super.visitIfExpression(expression)
                dec()
            } else {
                super.visitIfExpression(expression)
            }
        }

        override fun visitLoopExpression(loopExpression: KtLoopExpression) {
            inc()
            super.visitLoopExpression(loopExpression)
            dec()
        }

        override fun visitWhenExpression(expression: KtWhenExpression) {
            inc()
            super.visitWhenExpression(expression)
            dec()
        }

        override fun visitTryExpression(expression: KtTryExpression) {
            inc()
            super.visitTryExpression(expression)
            dec()
        }

        override fun visitCallExpression(expression: KtCallExpression) {
            val lambdaArguments = expression.lambdaArguments
            if (expression.isUsedForNesting()) {
                insideLambdaDo(lambdaArguments) { inc() }
                super.visitCallExpression(expression)
                insideLambdaDo(lambdaArguments) { dec() }
            }
        }

        private fun insideLambdaDo(lambdaArguments: List<KtLambdaArgument>, function: () -> Unit) {
            if (lambdaArguments.isNotEmpty()) {
                val lambdaArgument = lambdaArguments[0]
                if (lambdaArgument.getLambdaExpression()?.bodyExpression != null) {
                    function()
                }
            }
        }

        private fun KtCallExpression.isUsedForNesting(): Boolean =
            getCallNameExpression()?.text in setOf("run", "let", "apply", "with", "use", "forEach")
    }
}

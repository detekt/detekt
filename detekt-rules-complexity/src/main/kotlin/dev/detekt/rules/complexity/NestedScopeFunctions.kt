package dev.detekt.rules.complexity

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.DetektVisitor
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.psi.FunctionMatcher
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaCallableSymbol
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * Although the scope functions are a way of making the code more concise, avoid overusing them: it can decrease
 * your code readability and lead to errors. Avoid nesting scope functions and be careful when chaining them:
 * it's easy to get confused about the current context object and the value of this or it.
 *
 * [Reference](https://kotlinlang.org/docs/scope-functions.html)
 *
 * <noncompliant>
 * // Try to figure out, what changed, without knowing the details
 * first.apply {
 *     second.apply {
 *         b = a
 *         c = b
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * // 'a' is a property of current class
 * // 'b' is a property of class 'first'
 * // 'c' is a property of class 'second'
 * first.b = this.a
 * second.c = first.b
 * </compliant>
 */
class NestedScopeFunctions(config: Config) :
    Rule(
        config,
        "Over-using scope functions makes code confusing, hard to read and bug prone."
    ),
    RequiresAnalysisApi {

    @Configuration("The maximum allowed depth for nested scope functions.")
    private val allowedDepth: Int by config(defaultValue = 1)

    @Configuration(
        "Set of scope function names which add complexity. " +
            "Function names have to be fully qualified. For example 'kotlin.apply'."
    )
    private val functions: List<FunctionMatcher> by config(DEFAULT_FUNCTIONS) {
        it.toSet().map(FunctionMatcher::fromFunctionSignature)
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        function.accept(FunctionDepthVisitor())
    }

    private fun report(element: KtCallExpression, depth: Int) {
        val finding = Finding(
            Entity.from(element),
            "The scope function '${element.calleeExpression?.text}' is nested too deeply ('$depth'). " +
                "The maximum allowed depth is set to '$allowedDepth'."
        )
        report(finding)
    }

    private companion object {
        val DEFAULT_FUNCTIONS = listOf(
            "kotlin.apply",
            "kotlin.run",
            "kotlin.with",
            "kotlin.let",
            "kotlin.also",
        )
    }

    private inner class FunctionDepthVisitor : DetektVisitor() {
        private var depth = 0

        override fun visitCallExpression(expression: KtCallExpression) {
            fun callSuper(): Unit = super.visitCallExpression(expression)

            if (expression.isScopeFunction()) {
                doWithIncrementedDepth {
                    reportIfOverThreshold(expression)
                    callSuper()
                }
            } else {
                callSuper()
            }
        }

        private fun doWithIncrementedDepth(block: () -> Unit) {
            depth++
            block()
            depth--
        }

        private fun reportIfOverThreshold(expression: KtCallExpression) {
            if (depth > allowedDepth) {
                report(expression, depth)
            }
        }

        private fun KtCallExpression.isScopeFunction(): Boolean =
            callableSymbols()?.any { it.matchesScopeFunction() } ?: false

        private fun KtCallExpression.callableSymbols() =
            analyze(this) {
                resolveToCall()?.singleFunctionCallOrNull()?.let {
                    sequence {
                        yield(it.symbol)
                        yieldAll(it.symbol.allOverriddenSymbols)
                    }
                }
            }

        private fun KaCallableSymbol.matchesScopeFunction(): Boolean = functions.any { it.match(this) }
    }
}

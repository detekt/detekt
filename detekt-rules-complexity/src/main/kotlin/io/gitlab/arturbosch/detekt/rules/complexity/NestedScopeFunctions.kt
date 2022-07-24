package io.gitlab.arturbosch.detekt.rules.complexity

import io.github.detekt.tooling.api.FunctionMatcher
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall

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
@RequiresTypeResolution
class NestedScopeFunctions(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Maintainability,
        "Over-using scope functions makes code confusing, hard to read and bug prone.",
        Debt.FIVE_MINS
    )

    @Configuration("Number of nested scope functions allowed.")
    private val threshold: Int by config(defaultValue = 1)

    @Configuration(
        "Set of scope function names which add complexity. " +
            "Function names have to be fully qualified. For example 'kotlin.apply'."
    )
    private val functions: List<FunctionMatcher> by config(DEFAULT_FUNCTIONS) {
        it.toSet().map(FunctionMatcher::fromFunctionSignature)
    }

    override fun visitCondition(root: KtFile) = bindingContext != BindingContext.EMPTY && super.visitCondition(root)

    override fun visitNamedFunction(function: KtNamedFunction) {
        function.accept(FunctionDepthVisitor())
    }

    private fun report(element: KtCallExpression, depth: Int) {
        val finding = ThresholdedCodeSmell(
            issue,
            Entity.from(element),
            Metric("SIZE", depth, threshold),
            "The scope function '${element.calleeExpression?.text}' is nested too deeply ('$depth'). " +
                "Complexity threshold is set to '$threshold'."
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
            if (depth > threshold) {
                report(expression, depth)
            }
        }

        private fun KtCallExpression.isScopeFunction(): Boolean {
            val descriptors = resolveDescriptors()
            return !descriptors.any { it.matchesScopeFunction() }
        }

        private fun KtCallExpression.resolveDescriptors(): List<CallableDescriptor> =
            getResolvedCall(bindingContext)?.resultingDescriptor
                ?.let { listOf(it) + it.overriddenDescriptors }
                .orEmpty()

        private fun CallableDescriptor.matchesScopeFunction(): Boolean =
            !functions.any { it.match(this) }
    }
}

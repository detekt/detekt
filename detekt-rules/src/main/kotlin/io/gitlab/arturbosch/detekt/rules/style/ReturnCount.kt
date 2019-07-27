package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SplitPattern
import io.gitlab.arturbosch.detekt.rules.parentsOfTypeUntil
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression

/**
 * Restrict the number of return methods allowed in methods.
 *
 * Having many exit points in a function can be confusing and impacts readability of the
 * code.
 *
 * <noncompliant>
 * fun foo(i: Int): String {
 *     when (i) {
 *         1 -> return "one"
 *         2 -> return "two"
 *         else -> return "other"
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo(i: Int): String {
 *     return when (i) {
 *         1 -> "one"
 *         2 -> "two"
 *         else -> "other"
 *     }
 * }
 * </compliant>
 *
 * @configuration max - define the maximum number of return statements allowed per function
 * (default: `2`)
 * @configuration excludedFunctions - define functions to be ignored by this check
 * (default: `"equals"`)
 * @configuration excludeLabeled - if labeled return statements should be ignored
 * (default: `false`)
 * @configuration excludeReturnFromLambda - if labeled return from a lambda should be ignored
 * (default: `true`)
 * @active since v1.0.0
 */
class ReturnCount(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName, Severity.Style,
            "Restrict the number of return statements in methods.", Debt.TEN_MINS)

    private val max = valueOrDefault(MAX, 2)
    private val excludedFunctions = SplitPattern(valueOrDefault(EXCLUDED_FUNCTIONS, ""))
    private val excludeLabeled = valueOrDefault(EXCLUDE_LABELED, false)
    private val excludeLambdas = valueOrDefault(EXCLUDE_RETURN_FROM_LAMBDA, true)

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        if (!isIgnoredFunction(function)) {
            val numberOfReturns = countFunctionReturns(function)

            if (numberOfReturns > max) {
                report(CodeSmell(issue, Entity.from(function), "Function ${function.name} has " +
                        "$numberOfReturns return statements which exceeds the limit of $max."))
            }
        }
    }

    private fun isIgnoredFunction(function: KtNamedFunction) = excludedFunctions.contains(function.name)

    private fun countFunctionReturns(function: KtNamedFunction): Int {
        var returnsNumber = 0
        function.accept(object : DetektVisitor() {
            override fun visitKtElement(element: KtElement) {
                if (element is KtReturnExpression) {
                    if (excludeLabeled && element.labeledExpression != null) {
                        return
                    } else if (excludeLambdas && isNamedReturnFromLambda(element)) {
                        return
                    } else {
                        returnsNumber++
                    }
                }
                element.children
                        .filter { it !is KtNamedFunction }
                        .forEach { it.accept(this) }
            }
        })
        return returnsNumber
    }

    private fun isNamedReturnFromLambda(expression: KtReturnExpression): Boolean {
        val label = expression.labeledExpression
        if (label != null) {
            return expression.parentsOfTypeUntil<KtCallExpression, KtNamedFunction>()
                    .map { it.calleeExpression }
                    .mapNotNull { it as? KtNameReferenceExpression }
                    .map { it.text }
                    .any { it in label.text }
        }
        return false
    }

    companion object {
        const val MAX = "max"
        const val EXCLUDED_FUNCTIONS = "excludedFunctions"
        const val EXCLUDE_LABELED = "excludeLabeled"
        const val EXCLUDE_RETURN_FROM_LAMBDA = "excludeReturnFromLambda"
    }
}

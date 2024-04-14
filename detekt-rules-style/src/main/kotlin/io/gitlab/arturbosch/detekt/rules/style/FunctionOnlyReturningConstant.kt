package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.simplePatternToRegex
import io.gitlab.arturbosch.detekt.rules.isActual
import io.gitlab.arturbosch.detekt.rules.isOpen
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.psiUtil.containingClass

/**
 * A function that only returns a single constant can be misleading. Instead, prefer declaring the constant
 * as a `const val`.
 *
 * <noncompliant>
 * fun functionReturningConstantString() = "1"
 * </noncompliant>
 *
 * <compliant>
 * const val constantString = "1"
 * </compliant>
 */
@ActiveByDefault(since = "1.2.0")
class FunctionOnlyReturningConstant(config: Config) : Rule(
    config,
    "A function that only returns a constant is misleading. Consider declaring a constant instead."
) {

    @Configuration("if overridden functions should be ignored")
    private val ignoreOverridableFunction: Boolean by config(true)

    @Configuration("if actual functions should be ignored")
    private val ignoreActualFunction: Boolean by config(true)

    @Configuration("excluded functions")
    private val excludedFunctions: List<Regex> by config(emptyList<String>()) { it.map(String::simplePatternToRegex) }

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (isNotIgnored(function) &&
            isNotExcluded(function) &&
            isReturningAConstant(function)
        ) {
            report(
                CodeSmell(
                    Entity.atName(function),
                    "${function.nameAsSafeName} is returning a constant. Prefer declaring a constant instead."
                )
            )
        }
        super.visitNamedFunction(function)
    }

    private fun isNotIgnored(function: KtNamedFunction): Boolean =
        checkOverridableFunction(function) && checkActualFunction(function)

    private fun checkOverridableFunction(function: KtNamedFunction): Boolean =
        if (ignoreOverridableFunction) {
            !function.isOverride() && !function.isOpen() && !checkContainingInterface(function)
        } else {
            true
        }

    private fun checkContainingInterface(function: KtNamedFunction): Boolean {
        val containingClass = function.containingClass()
        return containingClass != null && containingClass.isInterface()
    }

    private fun checkActualFunction(function: KtNamedFunction): Boolean =
        if (ignoreActualFunction) {
            !function.isActual()
        } else {
            true
        }

    private fun isNotExcluded(function: KtNamedFunction) =
        function.name !in excludedFunctions

    private fun isReturningAConstant(function: KtNamedFunction) =
        isConstantExpression(function.bodyExpression) || returnsConstant(function)

    private fun isConstantExpression(expression: KtExpression?): Boolean {
        if (expression is KtConstantExpression) {
            return true
        }
        return expression is KtStringTemplateExpression && !expression.hasInterpolation()
    }

    private fun returnsConstant(function: KtNamedFunction): Boolean {
        val returnExpression = function.bodyExpression?.children?.singleOrNull() as? KtReturnExpression
        return isConstantExpression(returnExpression?.returnedExpression)
    }
}

private operator fun Iterable<Regex>.contains(input: String?): Boolean {
    return input != null && any { it.matches(input) }
}

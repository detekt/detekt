package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SplitPattern
import io.gitlab.arturbosch.detekt.rules.isOpen
import io.gitlab.arturbosch.detekt.rules.isOverridden
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

/**
 * A function that only returns a single constant can be misleading. Instead prefer to define the constant directly
 * as a `const val`.
 *
 * <noncompliant>
 * fun functionReturningConstantString() = "1"
 * </noncompliant>
 *
 * <compliant>
 * const val constantString = "1"
 * </compliant>
 *
 * @configuration ignoreOverridableFunction - if overriden functions should be ignored (default: true)
 * @configuration excludedFunctions - excluded functions (default: 'describeContents')
 * @author schalkms
 * @author Marvin Ramin
 */
class FunctionOnlyReturningConstant(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style,
			"A function that only returns a constant is misleading. " +
					"Consider declaring a constant instead",
			Debt.TEN_MINS)

	private val ignoreOverridableFunction = valueOrDefault(IGNORE_OVERRIDABLE_FUNCTION, true)
	private val excludedFunctions = SplitPattern(valueOrDefault(EXCLUDED_FUNCTIONS, ""))

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (checkOverridableFunction(function) && isNotExcluded(function) && isReturningAConstant(function)) {
			report(CodeSmell(issue, Entity.from(function),
					"${function.nameAsSafeName} is returning a constant. Prefer declaring a constant instead."))
		}
		super.visitNamedFunction(function)
	}

	private fun checkOverridableFunction(function: KtNamedFunction) =
			if (ignoreOverridableFunction) !function.isOverridden() && !function.isOpen() else true

	private fun isNotExcluded(function: KtNamedFunction) =
			!excludedFunctions.contains(function.name)

	private fun isReturningAConstant(function: KtNamedFunction) =
			isConstantExpression(function.bodyExpression) || returnsConstant(function)

	private fun isConstantExpression(expression: KtExpression?): Boolean {
		if (expression is KtConstantExpression) {
			return true
		}
		val stringTemplate = expression as? KtStringTemplateExpression
		return stringTemplate?.hasInterpolation() == false
	}

	private fun returnsConstant(function: KtNamedFunction): Boolean {
		val children = function.bodyExpression?.children
		if (children?.size == 1) {
			val returnExpression = children[0] as? KtReturnExpression
			return isConstantExpression(returnExpression?.returnedExpression)
		}
		return false
	}

	companion object {
		const val IGNORE_OVERRIDABLE_FUNCTION = "ignoreOverridableFunction"
		const val EXCLUDED_FUNCTIONS = "excludedFunctions"
	}
}

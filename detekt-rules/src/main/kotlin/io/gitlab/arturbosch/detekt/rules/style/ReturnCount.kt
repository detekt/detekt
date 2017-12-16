package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SplitPattern
import io.gitlab.arturbosch.detekt.rules.collectByType
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
 * (default: 2)
 * @configuration excludedFunctions - define functions to be ignored by this check
 * (default: "equals")
 * @active since v1.0.0
 *
 * @author Niklas Baudy
 * @author schalkms
 * @author Marvin Ramin
 * @author Patrick Pilch
 */
class ReturnCount(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style,
			"Restrict the number of return statements in methods.", Debt.TEN_MINS)

	private val max = valueOrDefault(MAX, 2)
	private val excludedFunctions = SplitPattern(valueOrDefault(EXCLUDED_FUNCTIONS, ""))

	override fun visitNamedFunction(function: KtNamedFunction) {
		super.visitNamedFunction(function)

		if (!isIgnoredFunction(function)) {
			val numberOfReturns = function.collectByType<KtReturnExpression>().count()

			if (numberOfReturns > max) {
				report(CodeSmell(issue, Entity.from(function), "Function ${function.name} has " +
						"$numberOfReturns return statements which exceeds the limit of $max."))
			}
		}
	}

	private fun isIgnoredFunction(function: KtNamedFunction) = excludedFunctions.contains(function.name)

	companion object {
		const val MAX = "max"
		const val EXCLUDED_FUNCTIONS = "excludedFunctions"
	}
}

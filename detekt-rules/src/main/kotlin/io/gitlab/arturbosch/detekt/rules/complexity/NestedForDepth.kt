package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.rules.collectByType
import org.jetbrains.kotlin.psi.KtForExpression

/**
 * This rule reports excessive nesting depth of `for` expressions. Excessively nested for loops can be
 * harder to manage in one's head, and commonly masks the intended behavior of the function.
 *
 * Prefer extracting the nested code into well-named functions and/or using expressive comprehensions to
 * make it easier to understand.
 *
 * @configuration threshold - maximum allowed nesting depth (default: 2)
 *
 * @author Bob Boring
 */
class NestedForDepth(config: Config = Config.empty,
					 threshold: Int = DEFAULT_ACCEPTED_NESTING) : ThresholdRule(config, threshold) {

	override val issue = Issue("NestedForDepth",
			Severity.Maintainability,
			"Excessive nesting leads to hidden complexity and obscures meaning. " +
					"Prefer extracting code to make it easier to understand.",
			Debt.TWENTY_MINS)

	private val seenFors: MutableSet<KtForExpression> = mutableSetOf()

	override fun visitForExpression(expression: KtForExpression) {
		// filter out fors that are nested within other fors
		if (expression !in seenFors) {
			checkDepthOf(expression)
		}
		super.visitForExpression(expression)
	}

	private fun checkDepthOf(expression: KtForExpression) {
		val nestedFors = expression.collectByType<KtForExpression>()
		seenFors.addAll(nestedFors)
		if (nestedFors.size > threshold) {
			report(ThresholdedCodeSmell(issue,
					Entity.from(expression),
					Metric("NESTING DEPTH", nestedFors.size, threshold),
					"These for expressions are nested too deeply."))
		}
	}

	companion object {

		const val DEFAULT_ACCEPTED_NESTING = 2
	}
}

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
import org.jetbrains.kotlin.psi.KtTryExpression

/**
 * This rule reports excessive nesting depth of `try` blocks. Excessively nested `try` blocks greatly increase the
 * complexity of a function, and frequently signal that a function is taking on too much responsibility.
 *
 * Prefer extracting the nested code into well-named functions to make it easier to understand.
 *
 * @configuration threshold - maximum allowed nesting depth (default: 1)
 *
 * @author Bob Boring
 */
class NestedTryDepth(config: Config = Config.empty,
					 threshold: Int = DEFAULT_ACCEPTED_NESTING) : ThresholdRule(config, threshold) {

	override val issue = Issue("NestedForDepth",
			Severity.Maintainability,
			"Excessive nesting leads to hidden complexity and obscures meaning. " +
					"Prefer extracting code to make it easier to understand.",
			Debt.TWENTY_MINS)

	private val seenTries: MutableSet<KtTryExpression> = mutableSetOf()

	override fun visitTryExpression(expression: KtTryExpression) {
		// filter out fors that are nested within other fors
		if (expression !in seenTries) {
			checkDepthOf(expression)
		}
		super.visitTryExpression(expression)
	}

	private fun checkDepthOf(expression: KtTryExpression) {
		val nestedTries = expression.collectByType<KtTryExpression>()
		seenTries.addAll(nestedTries)
		if (nestedTries.size > threshold) {
			report(ThresholdedCodeSmell(issue,
					Entity.from(expression),
					Metric("NESTING DEPTH", nestedTries.size, threshold),
					"These try blocks are nested too deeply."))
		}
	}

	companion object {

		const val DEFAULT_ACCEPTED_NESTING = 1
	}
}

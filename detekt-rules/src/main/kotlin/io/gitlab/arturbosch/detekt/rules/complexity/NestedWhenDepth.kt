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
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * This rule reports excessive nesting depth of `when` expressions. Excessively nested when expressions clutter code
 * and commonly indicate that a function may be handling too many disparate conditional branches.
 *
 * Prefer extracting the nested code into well-named functions to
 * make it easier to understand.
 *
 * @configuration threshold - maximum allowed nesting depth (default: 1)
 *
 * @author Bob Boring
 */
class NestedWhenDepth(config: Config = Config.empty,
					  threshold: Int = DEFAULT_ACCEPTED_NESTING) : ThresholdRule(config, threshold) {

	override val issue = Issue("NestedWhenDepth",
			Severity.Maintainability,
			"Excessive nesting leads to hidden complexity and obscures meaning. " +
					"Prefer extracting code to make it easier to understand.",
			Debt.TWENTY_MINS)

	private val seenWhens: MutableSet<KtWhenExpression> = mutableSetOf()

	override fun visitWhenExpression(expression: KtWhenExpression) {
		// filter out whens that are nested within other whens
		if (expression !in seenWhens) {
			checkDepthOf(expression)
		}
		super.visitWhenExpression(expression)
	}

	private fun checkDepthOf(expression: KtWhenExpression) {
		val nestedWhens = expression.collectByType<KtWhenExpression>()
		seenWhens.addAll(nestedWhens)
		if (nestedWhens.size > threshold) {
			report(ThresholdedCodeSmell(issue,
					Entity.from(expression),
					Metric("NESTING DEPTH", nestedWhens.size, threshold),
					"These when expressions are nested too deeply."))
		}
	}

	companion object {

		const val DEFAULT_ACCEPTED_NESTING = 1
	}
}

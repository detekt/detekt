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
import org.jetbrains.kotlin.psi.KtContainerNodeForControlStructureBody
import org.jetbrains.kotlin.psi.KtIfExpression

/**
 * This rule reports excessive nesting depth of `if` expressions. Excessively nested `if`s increase the complexity of
 * a function by increasing the number of possible execution paths and state dependencies.
 *
 * Prefer simplifying the conditional logic or extracting the nested code into well-named functions
 * to make it easier to understand.
 *
 * @configuration threshold - maximum allowed nesting depth (default: 2)
 *
 * @author Bob Boring
 */
class NestedIfDepth(config: Config = Config.empty,
					threshold: Int = DEFAULT_ACCEPTED_NESTING) : ThresholdRule(config, threshold) {

	override val issue = Issue("NestedIfDepth",
			Severity.Maintainability,
			"Excessive nesting leads to hidden complexity and obscures meaning. " +
					"Prefer extracting code to make it easier to understand.",
			Debt.TWENTY_MINS)

	private val seenIfs: MutableSet<KtIfExpression> = mutableSetOf()

	override fun visitIfExpression(expression: KtIfExpression) {
		// filter out ifs that are nested within other ifs
		if (expression !in seenIfs) {
			checkDepthOf(expression)
		}
		super.visitIfExpression(expression)
	}

	private fun checkDepthOf(expression: KtIfExpression) {
		val nestedIfs = expression.collectByType<KtIfExpression>()
				.filter { it.parent !is KtContainerNodeForControlStructureBody }
		seenIfs.addAll(nestedIfs)
		if (nestedIfs.size > threshold) {
			report(ThresholdedCodeSmell(issue,
					Entity.from(expression),
					Metric("NESTING DEPTH", nestedIfs.size, threshold),
					"These if expressions are nested too deeply."))
		}
	}

	companion object {

		const val DEFAULT_ACCEPTED_NESTING = 2
	}
}

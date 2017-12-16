package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.rules.isUsedForNesting
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtContainerNodeForControlStructureBody
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtLoopExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * @configuration threshold - maximum nesting depth (default: 3)
 *
 * @active since v1.0.0
 * @author Artur Bosch
 * @author Marvin Ramin
 */
class NestedBlockDepth(config: Config = Config.empty,
					   threshold: Int = DEFAULT_ACCEPTED_NESTING) : ThresholdRule(config, threshold) {

	override val issue = Issue("NestedBlockDepth",
			Severity.Maintainability,
			"Excessive nesting leads to hidden complexity. " +
					"Prefer extracting code to make it easier to understand.")

	override fun visitNamedFunction(function: KtNamedFunction) {
		val visitor = FunctionDepthVisitor(threshold)
		visitor.visitNamedFunction(function)
		if (visitor.isTooDeep)
			report(ThresholdedCodeSmell(issue,
					Entity.from(function),
					Metric("SIZE", visitor.maxDepth, threshold),
					"Function ${function.name} is nested too deeply."))
	}

	private class FunctionDepthVisitor(val threshold: Int) : DetektVisitor() {

		internal var depth = 0
		internal var maxDepth = 0
		internal var isTooDeep = false
		private fun inc() {
			depth++
			if (depth > threshold) {
				isTooDeep = true
				if (depth > maxDepth) maxDepth = depth
			}
		}

		private fun dec() {
			depth--
		}

		override fun visitIfExpression(expression: KtIfExpression) {
			// Prevents else if (...) to count as two - #51C
			if (expression.parent !is KtContainerNodeForControlStructureBody) {
				inc()
				super.visitIfExpression(expression)
				dec()
			}
		}

		override fun visitLoopExpression(loopExpression: KtLoopExpression) {
			inc()
			super.visitLoopExpression(loopExpression)
			dec()
		}

		override fun visitWhenExpression(expression: KtWhenExpression) {
			inc()
			super.visitWhenExpression(expression)
			dec()
		}

		override fun visitTryExpression(expression: KtTryExpression) {
			inc()
			super.visitTryExpression(expression)
			dec()
		}

		override fun visitCallExpression(expression: KtCallExpression) {
			val lambdaArguments = expression.lambdaArguments
			if (expression.isUsedForNesting()) {
				insideLambdaDo(lambdaArguments) { inc() }
				super.visitCallExpression(expression)
				insideLambdaDo(lambdaArguments) { dec() }
			}
		}

		private fun insideLambdaDo(lambdaArguments: List<KtLambdaArgument>, function: () -> Unit) {
			if (lambdaArguments.isNotEmpty()) {
				val lambdaArgument = lambdaArguments[0]
				lambdaArgument.getLambdaExpression().bodyExpression?.let {
					function()
				}
			}
		}

	}
}

private const val DEFAULT_ACCEPTED_NESTING = 3

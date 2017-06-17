package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.*
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
 * @author Artur Bosch
 */
class NestedBlockDepth(config: Config = Config.empty, threshold: Int = 3) :
		ThresholdRule("NestedBlockDepth", config, threshold) {

	override fun visitNamedFunction(context: Context, function: KtNamedFunction) {
		val visitor = FunctionDepthVisitor(threshold)
		visitor.visitNamedFunction(context, function)
		if (visitor.isTooDeep)
			context.report(ThresholdedCodeSmell(ISSUE, Entity.from(function), Metric("SIZE", visitor.maxDepth, threshold)))
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

		override fun visitIfExpression(context: Context, expression: KtIfExpression) {
			// Prevents else if (...) to count as two - #51C
			if (expression.parent !is KtContainerNodeForControlStructureBody) {
				inc()
				super.visitIfExpression(context, expression)
				dec()
			}
		}

		override fun visitLoopExpression(context: Context, loopExpression: KtLoopExpression) {
			inc()
			super.visitLoopExpression(context, loopExpression)
			dec()
		}

		override fun visitWhenExpression(context: Context, expression: KtWhenExpression) {
			inc()
			super.visitWhenExpression(context, expression)
			dec()
		}

		override fun visitTryExpression(context: Context, expression: KtTryExpression) {
			inc()
			super.visitTryExpression(context, expression)
			dec()
		}

		override fun visitCallExpression(context: Context, expression: KtCallExpression) {
			val lambdaArguments = expression.lambdaArguments
			if (expression.isUsedForNesting()) {
				insideLambdaDo(lambdaArguments) { inc() }
				super.visitCallExpression(context, expression)
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

	companion object {
		val ISSUE = Issue("NestedBlockDepth", Issue.Severity.CodeSmell)
	}
}
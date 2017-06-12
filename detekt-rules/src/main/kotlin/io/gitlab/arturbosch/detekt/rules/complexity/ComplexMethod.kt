package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.*
import io.gitlab.arturbosch.detekt.rules.isUsedForNesting
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtLoopExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * @author Artur Bosch
 */
class ComplexMethod(config: Config = Config.empty, threshold: Int = 10) :
		ThresholdRule("ComplexMethod", config, threshold) {

	override fun visitNamedFunction(context: Context, function: KtNamedFunction) {
		val mcc = MccVisitor().visit(context, function)
		if (mcc > threshold) {
			context.report(ThresholdedCodeSmell(ISSUE, Entity.Companion.from(function), Metric("MCC", mcc, threshold)))
		}
	}

	internal class MccVisitor : DetektVisitor() {

		private var mcc = 1

		private fun inc() {
			mcc++
		}

		fun visit(context: Context, function: KtNamedFunction): Int {
			mcc = 1
			super.visitNamedFunction(context, function)
			return mcc
		}

		override fun visitIfExpression(context: Context, expression: KtIfExpression) {
			inc()
			super.visitIfExpression(context, expression)
		}

		override fun visitLoopExpression(context: Context, loopExpression: KtLoopExpression) {
			inc()
			super.visitLoopExpression(context, loopExpression)
		}

		override fun visitWhenExpression(context: Context, expression: KtWhenExpression) {
			inc()
			super.visitWhenExpression(context, expression)
		}

		override fun visitTryExpression(context: Context, expression: KtTryExpression) {
			inc()
			super.visitTryExpression(context, expression)
		}

		override fun visitCallExpression(context: Context, expression: KtCallExpression) {
			if (expression.isUsedForNesting()) {
				val lambdaArguments = expression.lambdaArguments
				if (lambdaArguments.size > 0) {
					val lambdaArgument = lambdaArguments[0]
					lambdaArgument.getLambdaExpression().bodyExpression?.let {
						inc()
					}
				}
			}
			super.visitCallExpression(context, expression)
		}
	}

	companion object {
		val ISSUE = Issue("ComplexMethod", Issue.Severity.CodeSmell)
	}
}
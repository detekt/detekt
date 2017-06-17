package io.gitlab.arturbosch.detekt.core.visitors

import io.gitlab.arturbosch.detekt.api.Context
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtLoopExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression

/**
 * @author Artur Bosch
 */
class McCabeVisitor : DetektVisitor() {

	var mcc = 0

	private fun inc() {
		mcc++
	}

	fun visit(context: Context, function: KtNamedFunction): Int {
		mcc = 0
		super.visitNamedFunction(context, function)
		return mcc
	}

	override fun visitNamedFunction(context: Context, function: KtNamedFunction) {
		inc()
		super.visitNamedFunction(context, function)
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

	fun KtCallExpression.isUsedForNesting(): Boolean = when (getCallNameExpression()?.text) {
		"run", "let", "apply", "with", "use", "forEach" -> true
		else -> false
	}
}
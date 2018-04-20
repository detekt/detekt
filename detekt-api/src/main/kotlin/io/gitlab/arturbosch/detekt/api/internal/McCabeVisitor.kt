package io.gitlab.arturbosch.detekt.api.internal

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

	override fun visitNamedFunction(function: KtNamedFunction) {
		inc()
		super.visitNamedFunction(function)
	}

	override fun visitIfExpression(expression: KtIfExpression) {
		inc()
		if (expression.`else` != null) {
			inc()
		}
		super.visitIfExpression(expression)
	}

	override fun visitLoopExpression(loopExpression: KtLoopExpression) {
		inc()
		super.visitLoopExpression(loopExpression)
	}

	override fun visitWhenExpression(expression: KtWhenExpression) {
		mcc += expression.entries.size
		super.visitWhenExpression(expression)
	}

	override fun visitTryExpression(expression: KtTryExpression) {
		inc()
		mcc += expression.catchClauses.size
		expression.finallyBlock?.let { inc() }
		super.visitTryExpression(expression)
	}

	override fun visitCallExpression(expression: KtCallExpression) {
		if (expression.isUsedForNesting()) {
			val lambdaArguments = expression.lambdaArguments
			if (lambdaArguments.size > 0) {
				val lambdaArgument = lambdaArguments[0]
				lambdaArgument.getLambdaExpression()?.bodyExpression?.let {
					inc()
				}
			}
		}
		super.visitCallExpression(expression)
	}
}

fun KtCallExpression.isUsedForNesting(): Boolean = when (getCallNameExpression()?.text) {
	"run", "let", "apply", "with", "use", "forEach" -> true
	else -> false
}

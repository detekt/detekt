package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtLoopExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression

/**
 * @author Artur Bosch
 */
class McCabeVisitor(private val ignoreSimpleWhenEntries: Boolean) : DetektVisitor() {

	var mcc: Int = 0
		private set(value) {
			field = value
		}

	override fun visitNamedFunction(function: KtNamedFunction) {
		mcc++
		super.visitNamedFunction(function)
	}

	override fun visitIfExpression(expression: KtIfExpression) {
		mcc++
		if (expression.`else` != null) {
			mcc++
		}
		super.visitIfExpression(expression)
	}

	override fun visitLoopExpression(loopExpression: KtLoopExpression) {
		mcc++
		super.visitLoopExpression(loopExpression)
	}

	override fun visitWhenExpression(expression: KtWhenExpression) {
		val entries = expression.extractEntries(ignoreSimpleWhenEntries)
		mcc += if (ignoreSimpleWhenEntries && entries.count() == 0) 1 else entries.count()
		super.visitWhenExpression(expression)
	}

	private fun KtWhenExpression.extractEntries(ignoreSimpleWhenEntries: Boolean): Sequence<KtWhenEntry> {
		val entries = entries.asSequence()
		return if (ignoreSimpleWhenEntries) entries.filter { it.expression is KtBlockExpression } else entries
	}

	override fun visitTryExpression(expression: KtTryExpression) {
		mcc++
		mcc += expression.catchClauses.size
		expression.finallyBlock?.let {
			mcc++
			Unit
		}
		super.visitTryExpression(expression)
	}

	override fun visitCallExpression(expression: KtCallExpression) {
		if (expression.isUsedForNesting()) {
			val lambdaArguments = expression.lambdaArguments
			if (lambdaArguments.size > 0) {
				val lambdaArgument = lambdaArguments[0]
				lambdaArgument.getLambdaExpression()?.bodyExpression?.let {
					mcc++
					Unit
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

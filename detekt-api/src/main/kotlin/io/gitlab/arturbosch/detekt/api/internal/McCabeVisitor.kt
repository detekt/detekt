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

	private var _mcc: Int = 0

	val mcc: Int
		get() = _mcc

	private fun inc() {
		_mcc++
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
		val entriesSequence = expression.extractEntries(ignoreSimpleWhenEntries)
		_mcc += entriesSequence.count()
		super.visitWhenExpression(expression)
	}

	private fun KtWhenExpression.extractEntries(ignoreSimpleWhenEntries: Boolean): Sequence<KtWhenEntry> {
		val entries = entries.asSequence()
		return if (ignoreSimpleWhenEntries) entries.filter { it.expression is KtBlockExpression } else entries
	}

	override fun visitTryExpression(expression: KtTryExpression) {
		inc()
		_mcc += expression.catchClauses.size
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

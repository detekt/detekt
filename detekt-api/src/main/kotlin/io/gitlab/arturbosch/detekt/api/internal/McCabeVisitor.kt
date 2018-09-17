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
class McCabeVisitor(private val simpleWhenEntriesWeight: Double = WHEN_DEFAULT_SIMPLE_ENTRY_WEIGHT) : DetektVisitor() {

	private var doubleMcc = 0.0

	val mcc: Int
		get() = Math.floor(doubleMcc).toInt()

	private fun inc() {
		doubleMcc += 1.0
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
		doubleMcc += expression.entries
				.asSequence()
				.map { it.weight }
				.sum()
		super.visitWhenExpression(expression)
	}

	override fun visitTryExpression(expression: KtTryExpression) {
		inc()
		doubleMcc += expression.catchClauses.size
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

	private val KtWhenEntry.weight: Double
		get() = if (expression is KtBlockExpression) WHEN_BLOCK_ENTRY_WEIGHT else simpleWhenEntriesWeight
}

private const val WHEN_BLOCK_ENTRY_WEIGHT = 1.0
private const val WHEN_DEFAULT_SIMPLE_ENTRY_WEIGHT = 0.5

fun KtCallExpression.isUsedForNesting(): Boolean = when (getCallNameExpression()?.text) {
	"run", "let", "apply", "with", "use", "forEach" -> true
	else -> false
}

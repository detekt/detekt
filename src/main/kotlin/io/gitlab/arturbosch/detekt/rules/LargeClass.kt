package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.MetricThresholdCodeSmellRule
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtLoopExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * @author Artur Bosch
 */
class LargeClass(threshold: Int = 70) : MetricThresholdCodeSmellRule("LargeClass", threshold) {

	private var loc = 0

	private fun inc() {
		loc += 1
	}

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		loc = 0
		classOrObject.getBody()?.let {
			loc += it.declarations.size
		}
		inc()
		super.visitClassOrObject(classOrObject)
		if (loc > 70) {
			addFindings(CodeSmell(id, Location.of(classOrObject)))
		}
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		val body: KtBlockExpression? = function.bodyExpression.asBlockExpression()
		body?.let {
			loc += body.statements.size
		}
		super.visitNamedFunction(function)
	}

	override fun visitIfExpression(expression: KtIfExpression) {
		expression.then?.let {
			loc += it.children.size
		}
		expression.`else`?.let {
			loc += it.children.size
		}
		super.visitIfExpression(expression)
	}

	override fun visitLoopExpression(loopExpression: KtLoopExpression) {
		loopExpression.body?.let {
			loc += it.children.size
		}
		super.visitLoopExpression(loopExpression)
	}

	override fun visitWhenExpression(expression: KtWhenExpression) {
		loc += expression.children.filter { it is KtWhenEntry }.size
		super.visitWhenExpression(expression)
	}

	override fun visitTryExpression(expression: KtTryExpression) {
		loc += expression.tryBlock.statements.size
		loc += expression.catchClauses.size
		expression.catchClauses.map { it.catchBody?.children?.size }.forEach { loc += it ?: 0 }
		expression.finallyBlock?.finalExpression?.statements?.size?.let { loc += it }
		super.visitTryExpression(expression)
	}

	override fun visitCallExpression(expression: KtCallExpression) {
		val lambdaArguments = expression.lambdaArguments
		if (lambdaArguments.size > 0) {
			val lambdaArgument = lambdaArguments[0]
			lambdaArgument.getLambdaExpression().bodyExpression?.let {
				loc += it.statements.size
			}
		}
		super.visitCallExpression(expression)
	}
}
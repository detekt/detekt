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
import java.util.ArrayDeque

/**
 * @author Artur Bosch
 */
class LargeClass(threshold: Int = 70) : MetricThresholdCodeSmellRule("LargeClass", threshold) {

	private val locStack = ArrayDeque<Int>()

	private fun incHead() {
		addToHead(1)
	}

	private fun addToHead(amount: Int) {
		locStack.push(locStack.pop() + amount)
	}

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		locStack.push(0)
		classOrObject.getBody()?.let {
			addToHead(it.declarations.size)
		}
		incHead() // for class body
		super.visitClassOrObject(classOrObject)
		if (locStack.pop() > 70) {
			addFindings(CodeSmell(id, Location.of(classOrObject)))
		}
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		val body: KtBlockExpression? = function.bodyExpression.asBlockExpression()
		body?.let { addToHead(body.statements.size) }
		super.visitNamedFunction(function)
	}

	override fun visitIfExpression(expression: KtIfExpression) {
		expression.then?.let { addToHead(it.children.size) }
		expression.`else`?.let { addToHead(it.children.size) }
		super.visitIfExpression(expression)
	}

	override fun visitLoopExpression(loopExpression: KtLoopExpression) {
		loopExpression.body?.let { addToHead(it.children.size) }
		super.visitLoopExpression(loopExpression)
	}

	override fun visitWhenExpression(expression: KtWhenExpression) {
		addToHead(expression.children.filter { it is KtWhenEntry }.size)
		super.visitWhenExpression(expression)
	}

	override fun visitTryExpression(expression: KtTryExpression) {
		addToHead(expression.tryBlock.statements.size)
		addToHead(expression.catchClauses.size)
		expression.catchClauses.map { it.catchBody?.children?.size }.forEach { addToHead(it ?: 0) }
		expression.finallyBlock?.finalExpression?.statements?.size?.let { addToHead(it) }
		super.visitTryExpression(expression)
	}

	override fun visitCallExpression(expression: KtCallExpression) {
		val lambdaArguments = expression.lambdaArguments
		if (lambdaArguments.size > 0) {
			val lambdaArgument = lambdaArguments[0]
			lambdaArgument.getLambdaExpression().bodyExpression?.let {
				addToHead(it.statements.size)
			}
		}
		super.visitCallExpression(expression)
	}
}
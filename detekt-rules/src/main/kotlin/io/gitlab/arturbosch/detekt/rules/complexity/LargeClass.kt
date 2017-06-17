package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.*
import io.gitlab.arturbosch.detekt.rules.asBlockExpression
import org.jetbrains.kotlin.psi.*
import java.util.ArrayDeque

/**
 * @author Artur Bosch
 */
class LargeClass(config: Config = Config.empty, threshold: Int = 70) : ThresholdRule("LargeClass", config, threshold) {

	private val locStack = ArrayDeque<Int>()

	private fun incHead() {
		addToHead(1)
	}

	private fun addToHead(amount: Int) {
		locStack.push(locStack.pop() + amount)
	}

	override fun preVisit(context: Context, root: KtFile) {
		locStack.clear()
	}

	override fun visitClassOrObject(context: Context, classOrObject: KtClassOrObject) {
		locStack.push(0)
		classOrObject.getBody()?.let {
			addToHead(it.declarations.size)
		}
		incHead() // for class body
		super.visitClassOrObject(context, classOrObject)
		val loc = locStack.pop()
		if (loc > threshold) {
			context.report(ThresholdedCodeSmell(ISSUE, Entity.Companion.from(classOrObject), Metric("SIZE", loc, threshold)))
		}
	}

	/**
	 * Top level members must be skipped as loc stack can be empty - #64
	 */
	override fun visitProperty(context: Context, property: KtProperty) {
		if (property.isTopLevel) return
		super.visitProperty(context, property)
	}

	override fun visitNamedFunction(context: Context, function: KtNamedFunction) {
		if (function.isTopLevel) return
		val body: KtBlockExpression? = function.bodyExpression.asBlockExpression()
		body?.let { addToHead(body.statements.size) }
		super.visitNamedFunction(context, function)
	}

	override fun visitIfExpression(context: Context, expression: KtIfExpression) {
		expression.then?.let { addToHead(it.children.size) }
		expression.`else`?.let { addToHead(it.children.size) }
		super.visitIfExpression(context, expression)
	}

	override fun visitLoopExpression(context: Context, loopExpression: KtLoopExpression) {
		loopExpression.body?.let { addToHead(it.children.size) }
		super.visitLoopExpression(context, loopExpression)
	}

	override fun visitWhenExpression(context: Context, expression: KtWhenExpression) {
		addToHead(expression.children.filter { it is KtWhenEntry }.size)
		super.visitWhenExpression(context, expression)
	}

	override fun visitTryExpression(context: Context, expression: KtTryExpression) {
		addToHead(expression.tryBlock.statements.size)
		addToHead(expression.catchClauses.size)
		expression.catchClauses.map { it.catchBody?.children?.size }.forEach { addToHead(it ?: 0) }
		expression.finallyBlock?.finalExpression?.statements?.size?.let { addToHead(it) }
		super.visitTryExpression(context, expression)
	}

	override fun visitCallExpression(context: Context, expression: KtCallExpression) {
		val lambdaArguments = expression.lambdaArguments
		if (lambdaArguments.size > 0) {
			val lambdaArgument = lambdaArguments[0]
			lambdaArgument.getLambdaExpression().bodyExpression?.let {
				addToHead(it.statements.size)
			}
		}
		super.visitCallExpression(context, expression)
	}

	companion object {
		val ISSUE = Issue("LargeClass", Issue.Severity.CodeSmell)
	}
}
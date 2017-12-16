package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.rules.asBlockExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtLoopExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.psi.KtWhenExpression
import java.util.ArrayDeque

/**
 * @configuration threshold - maximum size of a class (default: 150)
 *
 * @active since v1.0.0
 * @author Artur Bosch
 * @author Marvin Ramin
 */
class LargeClass(config: Config = Config.empty,
				 threshold: Int = DEFAULT_ACCEPTED_CLASS_LENGTH) : ThresholdRule(config, threshold) {

	override val issue = Issue("LargeClass",
			Severity.Maintainability,
			"One class should have one responsibility. Large classes tend to handle many things at once. " +
					"Split up large classes into smaller classes that are easier to understand.")

	private val locStack = ArrayDeque<Int>()

	private fun incHead() {
		addToHead(1)
	}

	private fun addToHead(amount: Int) {
		locStack.push(locStack.pop() + amount)
	}

	override fun visitFile(file: PsiFile?) { //TODO
		locStack.clear()
		super.visitFile(file)
	}

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		locStack.push(0)
		classOrObject.getBody()?.let {
			addToHead(it.declarations.size)
		}
		incHead() // for class body
		super.visitClassOrObject(classOrObject)
		val loc = locStack.pop()
		if (loc > threshold) {
			report(ThresholdedCodeSmell(issue,
					Entity.from(classOrObject),
					Metric("SIZE", loc, threshold),
					"Class ${classOrObject.name} is too large. Consider splitting it into smaller pieces."))
		}
	}

	/**
	 * Top level members must be skipped as loc stack can be empty - #64
	 */
	override fun visitProperty(property: KtProperty) {
		if (property.isTopLevel) return
		super.visitProperty(property)
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.isTopLevel) return
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

private const val DEFAULT_ACCEPTED_CLASS_LENGTH = 70

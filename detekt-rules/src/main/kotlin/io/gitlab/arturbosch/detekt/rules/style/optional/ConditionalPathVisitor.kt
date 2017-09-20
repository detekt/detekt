package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * @author Artur Bosch
 */
class ConditionalPathVisitor(private val block: (KtReturnExpression) -> Unit) : DetektVisitor() {

	override fun visitIfExpression(expression: KtIfExpression) {
		if (expression.isLastStatement()) {
			expression.then?.checkIfReturnStatement()
			expression.`else`?.checkIfReturnStatement()
		}
		super.visitIfExpression(expression)
	}

	override fun visitTryExpression(expression: KtTryExpression) {
		if (expression.isLastStatement()) {
			expression.tryBlock.checkIfReturnStatement()
			expression.catchClauses.forEach {
				it.catchBody?.checkIfReturnStatement()
			}
		}
		super.visitTryExpression(expression)
	}

	override fun visitWhenExpression(expression: KtWhenExpression) {
		if (expression.isLastStatement()) {
			expression.entries.forEach {
				it.expression?.checkIfReturnStatement()
			}
		}
		super.visitWhenExpression(expression)
	}

	private fun KtExpression.isLastStatement(): Boolean {
		val parent = parent
		parent is KtBlockExpression
		return when (parent) {
			is KtReturnExpression, is KtProperty -> true
			is KtDeclarationWithBody -> parent.bodyExpression == this
			is KtBlockExpression -> parent.statements.lastOrNull()?.let { it == this } == true
			else -> false
		}
	}

	private fun KtExpression.checkIfReturnStatement() {
		if (this is KtReturnExpression && this.returnedExpression != null) block(this)
		else if (this is KtBlockExpression) {
			val last = this.statements.lastOrNull()
			if (last is KtReturnExpression && last.returnedExpression != null) block(last)
		}
	}

}

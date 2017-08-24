package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPostfixExpression
import org.jetbrains.kotlin.psi.KtReturnExpression

class UselessIncrement(config: Config = Config.empty) : Rule(config) {

	override val issue: Issue = Issue("UselessIncrement", Severity.Defect,
			"The incremented value is unused. The incremented value is replaced with the original value.")

	override fun visitReturnExpression(expression: KtReturnExpression) {
		val postfixExpression = expression.returnedExpression as? KtPostfixExpression
		if (postfixExpression != null) {
			report(postfixExpression)
		}
		getPostfixExpressionChilds(expression.returnedExpression)
				?.forEach { report(it) }
	}

	override fun visitBinaryExpression(expression: KtBinaryExpression) {
		val postfixExpression = expression.right as? KtPostfixExpression
		val leftIdentifierText = expression.left?.text
		checkPostfixExpression(postfixExpression, leftIdentifierText)
		getPostfixExpressionChilds(expression.right)
				?.forEach { checkPostfixExpression(it, leftIdentifierText) }
	}

	private fun checkPostfixExpression(postfixExpression: KtPostfixExpression?, leftIdentifierText: String?) {
		if (postfixExpression != null && leftIdentifierText == postfixExpression.firstChild?.text) {
			report(postfixExpression)
		}
	}

	private fun report(postfixExpression: KtPostfixExpression) {
		report(CodeSmell(issue, Entity.from(postfixExpression)))
	}

	private fun getPostfixExpressionChilds(expression: KtExpression?) =
			expression?.children?.filterIsInstance<KtPostfixExpression>()
}

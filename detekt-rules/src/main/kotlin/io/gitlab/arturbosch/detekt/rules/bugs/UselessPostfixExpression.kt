package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.lexer.KtTokens.MINUSMINUS
import org.jetbrains.kotlin.lexer.KtTokens.PLUSPLUS
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPostfixExpression
import org.jetbrains.kotlin.psi.KtReturnExpression

class UselessPostfixExpression(config: Config = Config.empty) : Rule(config) {

	override val issue: Issue = Issue("UselessPostfixExpression", Severity.Defect,
			"The incremented or decremented value is unused. This value is replaced with the original value.")

	override fun visitReturnExpression(expression: KtReturnExpression) {
		val postfixExpression = expression.returnedExpression?.asPostFixExpression()
		if (postfixExpression != null) {
			report(postfixExpression)
		}
		getPostfixExpressionChilds(expression.returnedExpression)
				?.forEach { report(it) }
	}

	override fun visitBinaryExpression(expression: KtBinaryExpression) {
		val postfixExpression = expression.right?.asPostFixExpression()
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

	private fun KtExpression.asPostFixExpression() = if (this is KtPostfixExpression &&
			(operationToken === PLUSPLUS || operationToken === MINUSMINUS)) this else null

	private fun report(postfixExpression: KtPostfixExpression) {
		report(CodeSmell(issue, Entity.from(postfixExpression)))
	}

	private fun getPostfixExpressionChilds(expression: KtExpression?) =
			expression?.children?.filterIsInstance<KtPostfixExpression>()
}

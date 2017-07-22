package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.*
import io.gitlab.arturbosch.detekt.rules.asBlockExpression
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.psi.*

class SafeCast(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style, "Safe cast instead of if-else-null", Debt.FIVE_MINS)
	private var identifier = ""

	override fun visitIfExpression(expression: KtIfExpression) {
		val condition = expression.condition as? KtIsExpression
		if (condition != null) {
			val leftHandSide = condition.leftHandSide as? KtNameReferenceExpression
			if (leftHandSide != null) {
				identifier = leftHandSide.text
				val thenClause = expression.then
				val elseClause = expression.`else`
				val result = when (condition.isNegated) {
					true -> isIfElseNull(elseClause, thenClause)
					false -> isIfElseNull(thenClause, elseClause)
				}
				if (result) {
					addReport(expression)
				}
			}
		}
	}

	private fun isIfElseNull(thenClause: KtExpression?, elseClause: KtExpression?): Boolean {
		val hasIdentifier = thenClause?.asBlockExpression()?.statements?.firstOrNull()?.text == identifier
		val elseStatement = elseClause?.asBlockExpression()?.statements?.firstOrNull()
		val hasNull = elseStatement is KtConstantExpression && elseStatement.node.elementType == KtNodeTypes.NULL
		return hasIdentifier && hasNull
	}

	private fun addReport(expression: KtIfExpression) {
		report(CodeSmell(issue, Entity.from(expression)))
	}
}

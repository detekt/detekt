package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.psi.KtContainerNodeForControlStructureBody
import org.jetbrains.kotlin.psi.KtIfExpression

class CollapsibleIfStatements(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("CollapsibleIfStatements", Severity.Style,
			"Two if statements which could be collapsed were detected. " +
					"These statements can be merged to improve readability.",
			Debt.FIVE_MINS)

	override fun visitIfExpression(expression: KtIfExpression) {
		if (isNotElseIfOrElse(expression) && hasOneKtIfExpression(expression)) {
			report(CodeSmell(issue, Entity.from(expression)))
		}
		super.visitIfExpression(expression)
	}

	private fun isNotElseIfOrElse(expression: KtIfExpression) =
			expression.`else` == null && expression.parent !is KtContainerNodeForControlStructureBody

	private fun hasOneKtIfExpression(expression: KtIfExpression): Boolean {
		val statements = expression.then?.children?.filterNot { it is PsiComment }
		return statements != null && statements.size == 1 && statements[0] is KtIfExpression
	}
}

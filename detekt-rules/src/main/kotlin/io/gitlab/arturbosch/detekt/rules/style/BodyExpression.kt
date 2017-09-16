package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtReturnExpression

/**
 * @author aballano
 */
class BodyExpression(config: Config = Config.empty) : Rule(config) {
	override val issue: Issue = Issue(javaClass.simpleName, Severity.Style,
			"Simple return statements can be declared as body expressions")

	override fun visitReturnExpression(expression: KtReturnExpression) {
		precededByStatements(expression)
		super.visitReturnExpression(expression)
	}

	private fun precededByStatements(expression: KtExpression) {
		val statements = (expression.parent as? KtBlockExpression)?.statements ?: return
		if (statements.size == 1 && statements.contains(expression)) report(CodeSmell(issue, Entity.from(expression)))
	}
}

package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Context
import io.gitlab.arturbosch.detekt.api.Issue
import org.jetbrains.kotlin.psi.KtIfExpression

/**
 * @author Artur Bosch
 */
class EmptyElseBlock(config: Config) : EmptyRule("EmptyElseBlock", config = config) {

	override fun visitIfExpression(context: Context, expression: KtIfExpression) {
		expression.`else`?.addFindingIfBlockExprIsEmpty(context, ISSUE)
	}

	companion object {
		val ISSUE = Issue("EmptyElseBlock", Issue.Severity.Minor)
	}
}
package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Context
import io.gitlab.arturbosch.detekt.api.Issue
import org.jetbrains.kotlin.psi.KtIfExpression

/**
 * @author Artur Bosch
 */
class EmptyIfBlock(config: Config) : EmptyRule("EmptyIfBlock", config = config) {

	override fun visitIfExpression(context: Context, expression: KtIfExpression) {
		expression.then?.addFindingIfBlockExprIsEmpty(context, ISSUE)
	}

	companion object {
		val ISSUE = Issue("EmptyIfBlock", Issue.Severity.Minor)
	}
}
package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Context
import io.gitlab.arturbosch.detekt.api.Issue
import org.jetbrains.kotlin.psi.KtForExpression

/**
 * @author Artur Bosch
 */
class EmptyForBlock(config: Config) : EmptyRule("EmptyForBlock", config = config) {

	override fun visitForExpression(context: Context, expression: KtForExpression) {
		expression.body?.addFindingIfBlockExprIsEmpty(context, ISSUE)
	}

	companion object {
		val ISSUE = Issue("EmptyForBlock", Issue.Severity.Minor)
	}
}
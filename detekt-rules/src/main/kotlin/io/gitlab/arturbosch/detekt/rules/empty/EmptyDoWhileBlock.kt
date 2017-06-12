package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Context
import io.gitlab.arturbosch.detekt.api.Issue
import org.jetbrains.kotlin.psi.KtDoWhileExpression

/**
 * @author Artur Bosch
 */
class EmptyDoWhileBlock(config: Config) : EmptyRule("EmptyDoWhileBlock", config = config) {

	override fun visitDoWhileExpression(context: Context, expression: KtDoWhileExpression) {
		expression.body?.addFindingIfBlockExprIsEmpty(context, ISSUE)
	}

	companion object {
		val ISSUE = Issue("EmptyDoWhileBlock", Issue.Severity.Minor)
	}
}
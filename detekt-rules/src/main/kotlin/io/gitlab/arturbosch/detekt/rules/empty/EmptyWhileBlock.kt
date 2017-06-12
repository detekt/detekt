package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Context
import io.gitlab.arturbosch.detekt.api.Issue
import org.jetbrains.kotlin.psi.KtWhileExpression

/**
 * @author Artur Bosch
 */
class EmptyWhileBlock(config: Config) : EmptyRule("EmptyWhileBlock", config = config) {

	override fun visitWhileExpression(context: Context, expression: KtWhileExpression) {
		expression.body?.addFindingIfBlockExprIsEmpty(context, ISSUE)
	}

	companion object {
		val ISSUE = Issue("EmptyWhileBlock", Issue.Severity.Minor)
	}

}
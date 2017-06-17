package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Context
import io.gitlab.arturbosch.detekt.api.Issue
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class EmptyFunctionBlock(config: Config) : EmptyRule("EmptyFunctionBlock", config = config) {

	override fun visitNamedFunction(context: Context, function: KtNamedFunction) {
		function.bodyExpression?.addFindingIfBlockExprIsEmpty(context, ISSUE)
	}

	companion object {
		val ISSUE = Issue("EmptyFunctionBlock", Issue.Severity.Minor)
	}

}

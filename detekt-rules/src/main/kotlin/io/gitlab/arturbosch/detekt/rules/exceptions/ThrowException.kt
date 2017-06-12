package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Context
import io.gitlab.arturbosch.detekt.api.Issue
import org.jetbrains.kotlin.psi.KtThrowExpression

/**
 * @author Artur Bosch
 */
class ThrowException(config: Config = Config.empty) : ExceptionsRule("ThrowException", config) {

	override fun visitThrowExpression(context: Context, expression: KtThrowExpression) {
		expression.addFindingIfThrowingClassMatchesExact(context, ISSUE) { "Exception" }
	}

	companion object {
		val ISSUE = Issue("ThrowException", Issue.Severity.Maintainability)
	}
}
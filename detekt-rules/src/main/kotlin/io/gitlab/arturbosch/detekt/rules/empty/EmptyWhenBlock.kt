package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * @author Artur Bosch
 */
class EmptyWhenBlock(config: Config) : EmptyRule("EmptyWhenBlock", config = config) {

	override fun visitWhenExpression(context: Context, expression: KtWhenExpression) {
		if (expression.entries.isEmpty()) {
			context.report(CodeSmell(ISSUE, Entity.from(expression)))
		}
	}

	companion object {
		val ISSUE = Issue("EmptyWhenBlock", Issue.Severity.Minor)
	}

}
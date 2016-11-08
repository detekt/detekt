package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * @author Artur Bosch
 */
class EmptyWhenBlock(config: Config) : EmptyRule("EmptyWhenBlock", config = config) {

	override fun visitWhenExpression(expression: KtWhenExpression) {
		if (expression.entries.isEmpty()) {
			addFindings(CodeSmell(id, Entity.from(expression)))
		}
	}

}
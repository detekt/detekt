package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtThrowExpression

/**
 * @author Artur Bosch
 */
class ThrowNullPointerException(config: Config = Config.empty) : ExceptionsRule(config) {

	override fun visitThrowExpression(expression: KtThrowExpression) {
		expression.addFindingIfThrowingClassMatchesExact { "NullPointerException" }
	}

}

package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtIfExpression

/**
 * @author Artur Bosch
 */
class EmptyElseBlock(config: Config) : EmptyRule(config) {

	override fun visitIfExpression(expression: KtIfExpression) {
		expression.`else`?.addFindingIfBlockExprIsEmpty()
	}

}

package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtForExpression

/**
 * @author Artur Bosch
 */
class EmptyForBlock(config: Config) : EmptyRule("EmptyForBlock", config = config) {

	override fun visitForExpression(expression: KtForExpression) {
		expression.body?.addFindingIfBlockExprIsEmpty()
	}
}
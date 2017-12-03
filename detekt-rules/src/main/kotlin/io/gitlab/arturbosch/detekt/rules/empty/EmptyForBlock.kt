package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtForExpression

/**
 * @active since v1.0.0
 * @author Artur Bosch
 * @author Marvin Ramin
 */
class EmptyForBlock(config: Config) : EmptyRule(config) {

	override fun visitForExpression(expression: KtForExpression) {
		expression.body?.addFindingIfBlockExprIsEmpty()
	}
}

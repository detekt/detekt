package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtForExpression

/**
 * Reports empty `for` loops. Empty blocks of code serve no purpose and should be removed.
 *
 * @active since v1.0.0
 * @author Artur Bosch
 * @author Marvin Ramin
 */
class EmptyForBlock(config: Config) : EmptyRule(config) {

	override fun visitForExpression(expression: KtForExpression) {
		expression.body?.addFindingIfBlockExprIsEmpty()
	}
}

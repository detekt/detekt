package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtIfExpression

/**
 * Reports empty `else` blocks. Empty blocks of code serve no purpose and should be removed.
 *
 * @active since v1.0.0
 * @author Artur Bosch
 * @author Marvin Ramin
 */
class EmptyElseBlock(config: Config) : EmptyRule(config) {

	override fun visitIfExpression(expression: KtIfExpression) {
		expression.`else`?.addFindingIfBlockExprIsEmpty()
	}

}

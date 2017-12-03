package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtWhileExpression

/**
 * @active since v1.0.0
 * @author Artur Bosch
 * @author Marvin Ramin
 */
class EmptyWhileBlock(config: Config) : EmptyRule(config) {

	override fun visitWhileExpression(expression: KtWhileExpression) {
		expression.body?.addFindingIfBlockExprIsEmpty()
	}

}

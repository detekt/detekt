package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtWhileExpression

/**
 * @author Artur Bosch
 */
class EmptyWhileBlock(config: Config) : EmptyRule("EmptyWhileBlock", config = config) {

	override fun visitWhileExpression(expression: KtWhileExpression) {
		expression.body?.addFindingIfBlockExprIsEmpty()
	}

}
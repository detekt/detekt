package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtDoWhileExpression

/**
 * @author Artur Bosch
 */
class EmptyDoWhileBlock(config: Config) : EmptyRule(config) {

	override fun visitDoWhileExpression(expression: KtDoWhileExpression) {
		expression.body?.addFindingIfBlockExprIsEmpty()
	}

}

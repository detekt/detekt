package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class EmptyFunctionBlock(config: Config) : EmptyRule("EmptyFunctionBlock", config = config) {

	override fun visitNamedFunction(function: KtNamedFunction) {
		function.bodyExpression?.addFindingIfBlockExprIsEmpty()
	}

}

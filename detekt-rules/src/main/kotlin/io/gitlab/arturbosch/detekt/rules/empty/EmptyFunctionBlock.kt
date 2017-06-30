package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class EmptyFunctionBlock(config: Config) : EmptyRule(config) {

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (!function.hasModifier(KtTokens.OVERRIDE_KEYWORD)) {
			function.bodyExpression?.addFindingIfBlockExprIsEmpty()
		}
	}

}

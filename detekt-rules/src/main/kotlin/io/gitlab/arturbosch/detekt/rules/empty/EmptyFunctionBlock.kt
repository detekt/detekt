package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class EmptyFunctionBlock(config: Config) : EmptyRule(config) {

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.isNotOverridden() && function.notMeantForOverriding()) {
			function.bodyExpression?.addFindingIfBlockExprIsEmpty()
		}
	}

	private fun KtNamedFunction.isNotOverridden() = !hasModifier(KtTokens.OVERRIDE_KEYWORD)
	private fun KtNamedFunction.notMeantForOverriding() =
			!(hasModifier(KtTokens.OPEN_KEYWORD) && hasModifier(KtTokens.PROTECTED_KEYWORD))
}

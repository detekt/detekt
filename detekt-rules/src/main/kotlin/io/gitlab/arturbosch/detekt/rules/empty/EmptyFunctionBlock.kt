package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.isOpen
import io.gitlab.arturbosch.detekt.rules.isOverridden
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * Reports empty functions. Empty blocks of code serve no purpose and should be removed.
 * This rule will not report functions overriding others.
 *
 * @configuration ignoreOverriddenFunctions - excludes overridden functions with an empty body (default: false)
 *
 * @active since v1.0.0
 * @author Artur Bosch
 * @author Marvin Ramin
 * @author schalkms
 */
class EmptyFunctionBlock(config: Config) : EmptyRule(config) {

	private val ignoreOverriddenFunctions = valueOrDefault(IGNORE_OVERRIDDEN_FUNCTIONS, false)

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.isOpen()) {
			return
		}
		val bodyExpression = function.bodyExpression
		if (!ignoreOverriddenFunctions) {
			if (function.isOverridden()) {
				bodyExpression?.addFindingIfBlockExprIsEmptyAndNotCommented()
			} else {
				bodyExpression?.addFindingIfBlockExprIsEmpty()
			}
		} else if (!function.isOverridden()) {
			bodyExpression?.addFindingIfBlockExprIsEmpty()
		}
	}

	companion object {
		const val IGNORE_OVERRIDDEN_FUNCTIONS = "ignoreOverriddenFunctions"
	}
}

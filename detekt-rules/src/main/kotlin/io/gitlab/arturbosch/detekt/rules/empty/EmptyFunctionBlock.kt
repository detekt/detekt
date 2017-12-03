package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.isOpen
import io.gitlab.arturbosch.detekt.rules.isOverridden
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.isProtected

/**
 * @active since v1.0.0
 * @author Artur Bosch
 * @author Marvin Ramin
 */
class EmptyFunctionBlock(config: Config) : EmptyRule(config) {

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (!function.isOverridden() && function.notMeantForOverriding()) {
			function.bodyExpression?.addFindingIfBlockExprIsEmpty()
		}
	}

	private fun KtNamedFunction.notMeantForOverriding() = !(isOpen() && isProtected())
}

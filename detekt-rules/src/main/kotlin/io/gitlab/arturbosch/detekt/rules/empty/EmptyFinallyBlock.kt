package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtFinallySection

/**
 * @active since v1.0.0
 * @author Artur Bosch
 * @author Marvin Ramin
 */
class EmptyFinallyBlock(config: Config) : EmptyRule(config) {

	override fun visitFinallySection(finallySection: KtFinallySection) {
		finallySection.finalExpression?.addFindingIfBlockExprIsEmpty()
	}

}

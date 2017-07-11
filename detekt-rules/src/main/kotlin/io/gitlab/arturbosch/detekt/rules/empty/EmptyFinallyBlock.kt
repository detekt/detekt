package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtFinallySection

/**
 * @author Artur Bosch
 */
class EmptyFinallyBlock(config: Config) : EmptyRule(config) {

	override fun visitFinallySection(finallySection: KtFinallySection) {
		finallySection.finalExpression?.addFindingIfBlockExprIsEmpty()
	}

}

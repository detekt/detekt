package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmellThresholdRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameterList

/**
 * @author Artur Bosch
 */
class LongParameterList(config: Config = Config.empty, threshold: Int = 5) : CodeSmellThresholdRule("LongParameterList", config, threshold) {

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.hasModifier(KtTokens.OVERRIDE_KEYWORD)) return
		function.valueParameterList?.checkThreshold()
	}

	private fun KtParameterList.checkThreshold() {
		val size = parameters.size
		if (size > threshold) {
			addFindings(ThresholdedCodeSmell(id, Entity.from(this), Metric("SIZE", size, threshold)))
		}
	}
}
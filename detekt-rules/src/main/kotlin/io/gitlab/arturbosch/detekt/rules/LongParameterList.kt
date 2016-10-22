package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmellThresholdRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import org.jetbrains.kotlin.psi.KtParameterList

/**
 * @author Artur Bosch
 */
class LongParameterList(config: Config = Config.EMPTY, threshold: Int = 5) : CodeSmellThresholdRule("LongParameterList", config, threshold) {

	override fun visitParameterList(list: KtParameterList) {
		val size = list.parameters.size
		if (size > threshold) {
			addFindings(ThresholdedCodeSmell(id, Entity.from(list), Metric("SIZE", size, threshold)))
		}
	}
}
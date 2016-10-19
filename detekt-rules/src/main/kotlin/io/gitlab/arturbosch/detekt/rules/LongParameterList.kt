package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.CodeSmellThresholdRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Location
import org.jetbrains.kotlin.psi.KtParameterList

/**
 * @author Artur Bosch
 */
class LongParameterList(config: Config = Config.EMPTY, threshold: Int = 5) : CodeSmellThresholdRule("LongParameterList", config, threshold) {

	override fun visitParameterList(list: KtParameterList) {
		if (list.parameters.size > threshold) {
			addFindings(CodeSmell(id, Location.of(list)))
		}
	}
}
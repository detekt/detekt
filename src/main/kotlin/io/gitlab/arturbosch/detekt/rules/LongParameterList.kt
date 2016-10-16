package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.MetricThresholdCodeSmellRule
import org.jetbrains.kotlin.psi.KtParameterList

/**
 * @author Artur Bosch
 */
class LongParameterList(threshold: Int = 5) : MetricThresholdCodeSmellRule("LongParameterList", threshold) {

	override fun visitParameterList(list: KtParameterList) {
		if (list.parameters.size > threshold) {
			addFindings(CodeSmell(id, Location.of(list)))
		}
	}
}
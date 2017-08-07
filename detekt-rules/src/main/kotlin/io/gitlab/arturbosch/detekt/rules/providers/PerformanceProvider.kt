package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.performance.ForEachOnRange
import io.gitlab.arturbosch.detekt.rules.performance.SpreadOperator
import io.gitlab.arturbosch.detekt.rules.performance.UnnecessaryTemporaryInstantiation

class PerformanceProvider : RuleSetProvider {

	override val ruleSetId: String = "performance"

	override fun instance(config: Config): RuleSet {
		return RuleSet(ruleSetId, listOf(
				ForEachOnRange(config),
				SpreadOperator(config),
				UnnecessaryTemporaryInstantiation(config)
		))
	}
}

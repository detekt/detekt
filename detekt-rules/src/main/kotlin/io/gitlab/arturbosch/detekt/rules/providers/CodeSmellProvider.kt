package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

/**
 * @author Artur Bosch
 */
class CodeSmellProvider : RuleSetProvider {

	override val ruleSetId: String = "code-smell"

	override fun instance(config: Config): RuleSet {
		return RuleSet(ruleSetId, listOf(
				//FeatureEnvy(config)
		))
	}
}

package io.gitlab.arturbosch.detekt.sampleruleset

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

/**
 * @author Artur Bosch
 */
class SampleProvider : RuleSetProvider {
	override fun instance(config: Config): RuleSet {
		return RuleSet("sample", listOf(
				TooManyFunctions(),
				TooManyFunctions2(config)
		))
	}
}
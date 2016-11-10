package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.formatting.ConsecutiveBlankLines
import io.gitlab.arturbosch.detekt.rules.formatting.Indentation

/**
 * @author Artur Bosch
 */
class FormattingProvider : RuleSetProvider {
	override fun instance(config: Config): RuleSet {
		val providerId = "formatting"
		val subConfig = config.subConfig(providerId)
		return RuleSet(providerId, listOf(
				Indentation(subConfig),
				ConsecutiveBlankLines(subConfig)
		))
	}
}
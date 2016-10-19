package io.gitlab.arturbosch.detekt.cli.debug

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

/**
 * @author Artur Bosch
 */
object DebugRuleSetProvider : RuleSetProvider {
	override fun instance(config: Config): RuleSet {
		return RuleSet("debug", listOf(
				ElementPrinter(),
				TokenPrinter()
		))
	}
}
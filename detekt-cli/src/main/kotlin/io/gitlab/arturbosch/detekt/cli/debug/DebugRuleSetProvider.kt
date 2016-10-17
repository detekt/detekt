package io.gitlab.arturbosch.detekt.cli.debug

import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

/**
 * @author Artur Bosch
 */
object DebugRuleSetProvider : RuleSetProvider {
	override fun instance(): RuleSet {
		return RuleSet("debug", listOf(
				ElementPrinter(),
				TokenPrinter()
		))
	}
}
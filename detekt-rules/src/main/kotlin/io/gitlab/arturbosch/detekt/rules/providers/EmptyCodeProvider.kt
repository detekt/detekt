package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.empty.EmptyBlocks

/**
 * The empty-blocks ruleset contains rules that will report empty blocks of code
 * which should be avoided.
 *
 * @active since v1.0.0
 * @author Artur Bosch
 */
class EmptyCodeProvider : RuleSetProvider {

	override val ruleSetId: String = "empty-blocks"

	override fun instance(config: Config): RuleSet {
		return RuleSet(ruleSetId, listOf(
				EmptyBlocks(config)
		))
	}

}

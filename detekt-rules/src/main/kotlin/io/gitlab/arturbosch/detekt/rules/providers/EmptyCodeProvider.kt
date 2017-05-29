package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.empty.EmptyCatchBlock
import io.gitlab.arturbosch.detekt.rules.empty.EmptyClassBlock
import io.gitlab.arturbosch.detekt.rules.empty.EmptyDoWhileBlock
import io.gitlab.arturbosch.detekt.rules.empty.EmptyElseBlock
import io.gitlab.arturbosch.detekt.rules.empty.EmptyFinallyBlock
import io.gitlab.arturbosch.detekt.rules.empty.EmptyForBlock
import io.gitlab.arturbosch.detekt.rules.empty.EmptyFunctionBlock
import io.gitlab.arturbosch.detekt.rules.empty.EmptyIfBlock
import io.gitlab.arturbosch.detekt.rules.empty.EmptyWhenBlock
import io.gitlab.arturbosch.detekt.rules.empty.EmptyWhileBlock

/**
 * @author Artur Bosch
 */
class EmptyCodeProvider : RuleSetProvider {

	override val ruleSetId: String = "empty-blocks"

	override fun instance(config: Config): RuleSet {
		return RuleSet(ruleSetId, listOf(
				EmptyCatchBlock(config),
				EmptyFinallyBlock(config),
				EmptyIfBlock(config),
				EmptyDoWhileBlock(config),
				EmptyWhileBlock(config),
				EmptyForBlock(config),
				EmptyElseBlock(config),
				EmptyFunctionBlock(config),
				EmptyClassBlock(config),
				EmptyWhenBlock(config)
		))
	}

}
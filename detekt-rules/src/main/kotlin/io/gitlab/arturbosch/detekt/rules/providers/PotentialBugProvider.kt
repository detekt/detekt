package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.bugs.NoElseInWhenExpression
import io.gitlab.arturbosch.detekt.rules.bugs.DuplicateCaseInWhenExpression

/**
 * @author Artur Bosch
 */
class PotentialBugProvider : RuleSetProvider {

	override fun instance(config: Config): RuleSet {
		return RuleSet("potential_bugs", listOf(
				NoElseInWhenExpression(config),
				DuplicateCaseInWhenExpression(config)
		))
	}

}
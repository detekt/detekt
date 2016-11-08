package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.bugs.DuplicateCaseInWhenExpression
import io.gitlab.arturbosch.detekt.rules.bugs.EqualsWithHashCodeExist
import io.gitlab.arturbosch.detekt.rules.bugs.NoElseInWhenExpression

/**
 * @author Artur Bosch
 */
class PotentialBugProvider : RuleSetProvider {

	override fun instance(config: Config): RuleSet {
		return RuleSet("potential-bugs", listOf(
				NoElseInWhenExpression(config),
				DuplicateCaseInWhenExpression(config),
				EqualsWithHashCodeExist(config)
		))
	}

}
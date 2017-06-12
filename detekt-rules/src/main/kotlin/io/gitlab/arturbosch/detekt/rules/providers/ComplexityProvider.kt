package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.complexity.*

/**
 * @author Artur Bosch
 */
class ComplexityProvider : RuleSetProvider {

	override val ruleSetId: String = "complexity"

	override fun instance(config: Config): RuleSet {
		return RuleSet(ruleSetId, listOf(
				LongParameterList(config),
				LongMethod(config),
				LargeClass(config),
				ComplexMethod(config),
				NestedBlockDepth(config),
				TooManyFunctions(config),
				ComplexCondition(config)
		))
	}
}
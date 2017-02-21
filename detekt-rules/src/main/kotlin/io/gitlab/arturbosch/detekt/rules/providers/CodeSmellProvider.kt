package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.ComplexMethod
import io.gitlab.arturbosch.detekt.rules.LargeClass
import io.gitlab.arturbosch.detekt.rules.LongMethod
import io.gitlab.arturbosch.detekt.rules.LongParameterList
import io.gitlab.arturbosch.detekt.rules.NestedBlockDepth

/**
 * @author Artur Bosch
 */
class CodeSmellProvider : RuleSetProvider {

	override val ruleSetId: String = "code-smell"

	override fun instance(config: Config): RuleSet {
		return RuleSet(ruleSetId, listOf(
				LongParameterList(config),
				LongMethod(config),
				LargeClass(config),
				ComplexMethod(config),
				NestedBlockDepth(config)/*,
				FeatureEnvy(config)*/
		))
	}
}
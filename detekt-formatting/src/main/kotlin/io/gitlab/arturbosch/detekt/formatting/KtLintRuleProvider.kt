package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

/**
 * @author Artur Bosch
 */
class KtLintRuleProvider : RuleSetProvider {

	override val ruleSetId: String = "formatting"

	override fun instance(config: Config) =
			RuleSet(ruleSetId, listOf(KtLintMultiRule(config)))
}

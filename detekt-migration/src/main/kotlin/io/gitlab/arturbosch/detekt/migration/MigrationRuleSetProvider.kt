package io.gitlab.arturbosch.detekt.migration

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

/**
 * @author Artur Bosch
 */
class MigrationRuleSetProvider : RuleSetProvider {

	override val ruleSetId: String = "migration"

	override fun instance(config: Config): RuleSet {
		return RuleSet(ruleSetId, listOf(MigrateImportsRule(config)))
	}
}

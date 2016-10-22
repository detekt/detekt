package io.gitlab.arturbosch.detekt.api

/**
 * @author Artur Bosch
 */
interface RuleSetProvider {
	fun buildRuleset(config: Config): RuleSet? {
		val ruleSet = instance(config)
		val subConfig = config.subConfig(ruleSet.id)
		val active = subConfig.valueOrDefault("active") { true }
		return if (active) ruleSet else null
	}

	fun instance(config: Config): RuleSet
}
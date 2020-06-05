package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

/**
 * Loads a [RuleSet] instance of given RuleSetProvider.
 */
inline fun <reified T : RuleSetProvider> loadRuleSet(config: Config = Config.empty): RuleSet {
    val provider = T::class.java.constructors[0].newInstance() as? T
        ?: error("Could not load RuleSet for '${T::class.java}'")
    return provider.instance(config.subConfig(provider.ruleSetId))
}

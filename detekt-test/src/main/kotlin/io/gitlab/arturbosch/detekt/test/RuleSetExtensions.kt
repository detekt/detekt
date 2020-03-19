package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.core.rules.createRuleSet

/**
 * Creates a RuleSet instance of given RuleSetProvider.
 */
inline fun <reified T : RuleSetProvider> loadRuleSet(config: Config = Config.empty) =
    (T::class.java.constructors[0].newInstance() as? T)
        ?.createRuleSet(config)
        ?: throw IllegalStateException("Could not load RuleSet for '${T::class.java}'")

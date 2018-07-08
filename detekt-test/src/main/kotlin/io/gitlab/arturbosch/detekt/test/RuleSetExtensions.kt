package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

/**
 * @author Artur Bosch
 */

inline fun <reified T : RuleSetProvider> loadRuleSet(config: Config = Config.empty) =
		(T::class.java.constructors[0].newInstance() as T).buildRuleset(config)
				?: throw IllegalStateException("Could not load RuleSet for '${T::class.java}'")

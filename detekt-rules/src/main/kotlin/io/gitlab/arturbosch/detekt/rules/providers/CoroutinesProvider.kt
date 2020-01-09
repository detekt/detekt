package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.coroutines.GlobalCoroutineUsage

/**
 * The coroutines rule set analyzes code for potential coroutines problems.
 *
 * @active since v1.4.0
 */
class CoroutinesProvider : RuleSetProvider {
    override val ruleSetId: String = "coroutines"

    override fun instance(config: Config): RuleSet {
        return RuleSet(ruleSetId, listOf(
            GlobalCoroutineUsage(config)
        ))
    }
}

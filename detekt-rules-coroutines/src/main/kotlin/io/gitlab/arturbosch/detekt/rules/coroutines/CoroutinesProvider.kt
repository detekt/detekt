package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider

/**
 * The coroutines rule set analyzes code for potential coroutines problems.
 *
 * @active since v1.4.0
 */
class CoroutinesProvider : DefaultRuleSetProvider {

    override val ruleSetId: String = "coroutines"

    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            GlobalCoroutineUsage(config),
            RedundantSuspendModifier(config),
            SuspendFunWithFlowReturnType(config)
        )
    )
}

package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider

/**
 * The empty-blocks ruleset contains rules that will report empty blocks of code
 * which should be avoided.
 *
 * @active since v1.0.0
 */
class EmptyCodeProvider : DefaultRuleSetProvider {

    override val ruleSetId: String = "empty-blocks"

    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            EmptyBlocks(config)
        )
    )
}

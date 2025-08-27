package dev.detekt.core.rules

import dev.detekt.api.RuleName
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetProvider

internal class SingleRuleProvider private constructor(
    override val ruleSetId: RuleSet.Id,
    private val ruleSet: RuleSet,
) : RuleSetProvider {

    override fun instance() = ruleSet

    companion object {
        operator fun invoke(ruleName: RuleName, wrapped: RuleSetProvider): SingleRuleProvider {
            val ruleProvider = requireNotNull(wrapped.instance().rules[ruleName]) {
                "There was not rule '$ruleName' in rule set '${wrapped.ruleSetId}'."
            }

            return SingleRuleProvider(
                ruleSetId = wrapped.ruleSetId,
                ruleSet = RuleSet(wrapped.ruleSetId, mapOf(ruleName to ruleProvider))
            )
        }
    }
}

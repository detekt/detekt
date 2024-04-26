package io.gitlab.arturbosch.detekt.core.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

internal class SingleRuleProvider(
    private val ruleName: Rule.Name,
    private val wrapped: RuleSetProvider
) : RuleSetProvider {

    init {
        createRuleInstance() // provoke early exit when rule does not exist
    }

    override val ruleSetId = wrapped.ruleSetId

    override fun instance(): RuleSet = RuleSet(
        ruleSetId,
        listOf(createRuleInstance())
    )

    private fun createRuleInstance(): (Config) -> Rule =
        requireNotNull(
            wrapped.instance().rules[ruleName]
        ) { "There was no rule '$ruleName' in rule set '${wrapped.ruleSetId}'." }
}

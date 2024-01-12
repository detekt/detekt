package io.gitlab.arturbosch.detekt.core.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleId
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

internal class SingleRuleProvider(
    private val ruleId: RuleId,
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
            wrapped.instance().rules[ruleId]
        ) { "There was no rule '$ruleId' in rule set '${wrapped.ruleSetId}'." }
}

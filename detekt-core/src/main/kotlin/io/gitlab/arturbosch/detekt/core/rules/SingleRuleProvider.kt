package io.gitlab.arturbosch.detekt.core.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleId
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.BaseRule

internal class SingleRuleProvider(
    private val ruleId: RuleId,
    private val wrapped: RuleSetProvider
) : RuleSetProvider {

    init {
        createRuleInstance(Config.empty) // provoke early exit when rule does not exist
    }

    override val ruleSetId: String = wrapped.ruleSetId

    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId,
        listOf(createRuleInstance(config))
    )

    private fun createRuleInstance(config: Config): BaseRule =
        requireNotNull(
            wrapped.instance(config)
                .rules
                .find { it.ruleId == ruleId }
        ) { "There was no rule '$ruleId' in rule set '${wrapped.ruleSetId}'." }
}

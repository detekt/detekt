package io.github.detekt.custom

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class CustomChecksProvider : RuleSetProvider {

    override val ruleSetId = RULE_SET_NAME

    override fun instance(config: Config) = RuleSet(
        ruleSetId,
        listOf(SpekTestDiscovery(config))
    )

    companion object {
        const val RULE_SET_NAME = "custom-checks"
    }
}

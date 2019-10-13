package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.junit.TestWithoutAssertion

/**
 * Rules in this rule set report issues related to JUnit tests
 *
 */
class JUnitProvider : RuleSetProvider {

    override val ruleSetId: String = "junit"

    override fun instance(config: Config): RuleSet {
        return RuleSet(
            ruleSetId, listOf(
                TestWithoutAssertion(config)
            )
        )
    }
}

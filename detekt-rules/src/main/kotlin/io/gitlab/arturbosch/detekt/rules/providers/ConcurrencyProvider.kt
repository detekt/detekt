package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.concurrency.GlobalScopeUsage

/** The concurrency rule set analyzes code for potential concurrency problems.
 *
 * @active since v1.3.0
 */
class ConcurrencyProvider : RuleSetProvider {
    override val ruleSetId: String = "performance"

    override fun instance(config: Config): RuleSet {
        return RuleSet(ruleSetId, listOf(
            GlobalScopeUsage(config)
        ))
    }
}

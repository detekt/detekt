package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider

/**
 * The performance rule set analyzes code for potential performance problems.
 *
 * @active since v1.0.0
 */
class PerformanceProvider : DefaultRuleSetProvider {

    override val ruleSetId: String = "performance"

    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            ForEachOnRange(config),
            SpreadOperator(config),
            UnnecessaryTemporaryInstantiation(config),
            ArrayPrimitive(config)
        )
    )
}

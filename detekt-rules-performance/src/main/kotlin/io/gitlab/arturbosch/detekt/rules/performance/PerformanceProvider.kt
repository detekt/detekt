package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider

/**
 * The performance rule set analyzes code for potential performance problems.
 */
@ActiveByDefault(since = "1.0.0")
class PerformanceProvider : DefaultRuleSetProvider {

    override val ruleSetId = RuleSet.Id("performance")

    override fun instance(): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            ::ForEachOnRange,
            ::SpreadOperator,
            ::UnnecessaryTemporaryInstantiation,
            ::UnnecessaryTypeCasting,
            ::ArrayPrimitive,
            ::CouldBeSequence,
            ::UnnecessaryPartOfBinaryExpression,
        )
    )
}

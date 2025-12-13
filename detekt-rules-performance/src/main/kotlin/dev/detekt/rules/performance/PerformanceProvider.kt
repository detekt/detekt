package dev.detekt.rules.performance

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.internal.DefaultRuleSetProvider

/**
 * The performance rule set analyzes code for potential performance problems.
 */
@ActiveByDefault(since = "1.0.0")
class PerformanceProvider : DefaultRuleSetProvider {

    override val ruleSetId = RuleSetId("performance")

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

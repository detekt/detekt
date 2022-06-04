package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider

/**
 * This rule set contains rules that report complex code.
 */
@ActiveByDefault(since = "1.0.0")
class ComplexityProvider : DefaultRuleSetProvider {

    override val ruleSetId: String = "complexity"

    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            LongParameterList(config),
            LongMethod(config),
            LargeClass(config),
            ComplexInterface(config),
            ComplexMethod(config),
            StringLiteralDuplication(config),
            MethodOverloading(config),
            NestedBlockDepth(config),
            NestedScopeFunctions(config),
            TooManyFunctions(config),
            ComplexCondition(config),
            LabeledExpression(config),
            ReplaceSafeCallChainWithRun(config),
            NamedArguments(config)
        )
    )
}

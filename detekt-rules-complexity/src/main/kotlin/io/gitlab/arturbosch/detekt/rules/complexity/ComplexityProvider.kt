package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider

/**
 * This rule set contains rules that report complex code.
 */
@ActiveByDefault(since = "1.0.0")
class ComplexityProvider : DefaultRuleSetProvider {

    override val ruleSetId = RuleSet.Id("complexity")

    override fun instance(): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            ::LongParameterList,
            ::LongMethod,
            ::LargeClass,
            ::ComplexInterface,
            ::CyclomaticComplexMethod,
            ::CognitiveComplexMethod,
            ::StringLiteralDuplication,
            ::MethodOverloading,
            ::NestedBlockDepth,
            ::NestedScopeFunctions,
            ::TooManyFunctions,
            ::ComplexCondition,
            ::LabeledExpression,
            ::ReplaceSafeCallChainWithRun,
            ::NamedArguments
        )
    )
}

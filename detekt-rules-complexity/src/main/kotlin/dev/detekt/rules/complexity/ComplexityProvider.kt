package dev.detekt.rules.complexity

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.RuleSet
import dev.detekt.api.internal.DefaultRuleSetProvider

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

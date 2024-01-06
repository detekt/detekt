package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider

/**
 * The naming ruleset contains rules which assert the naming of different parts of the codebase.
 */
@ActiveByDefault(since = "1.0.0")
class NamingProvider : DefaultRuleSetProvider {

    override val ruleSetId: String = "naming"

    override fun instance(): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            ::MatchingDeclarationName,
            ::MemberNameEqualsClassName,
            ::InvalidPackageDeclaration,
            ::NoNameShadowing,
            ::TopLevelPropertyNaming,
            ::BooleanPropertyNaming,
            ::LambdaParameterNaming,
            ::ConstructorParameterNaming,
            ::ForbiddenClassName,
            ::ClassNaming,
            ::PackageNaming,
            ::EnumNaming,
            ::ObjectPropertyNaming,
            ::FunctionParameterNaming,
            ::FunctionNaming,
            ::FunctionNameMinLength,
            ::FunctionNameMaxLength,
            ::VariableMaxLength,
            ::VariableMinLength,
            ::VariableNaming,
            ::NonBooleanPropertyPrefixedWithIs
        )
    )
}

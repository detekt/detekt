package dev.detekt.rules.naming

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.internal.DefaultRuleSetProvider

/**
 * The naming ruleset contains rules which assert the naming of different parts of the codebase.
 */
@ActiveByDefault(since = "1.0.0")
class NamingProvider : DefaultRuleSetProvider {

    override val ruleSetId = RuleSetId("naming")

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

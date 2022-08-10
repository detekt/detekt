package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider

/**
 * The naming ruleset contains rules which assert the naming of different parts of the codebase.
 */
@ActiveByDefault(since = "1.0.0")
class NamingProvider : DefaultRuleSetProvider {

    override val ruleSetId: String = "naming"

    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            MatchingDeclarationName(config),
            MemberNameEqualsClassName(config),
            InvalidPackageDeclaration(config),
            NoNameShadowing(config),
            TopLevelPropertyNaming(config),
            BooleanPropertyNaming(config),
            LambdaParameterNaming(config),
            ConstructorParameterNaming(config),
            ForbiddenClassName(config),
            ClassNaming(config),
            PackageNaming(config),
            EnumNaming(config),
            ObjectPropertyNaming(config),
            FunctionParameterNaming(config),
            FunctionNaming(config),
            FunctionMinLength(config),
            FunctionMaxLength(config),
            VariableMaxLength(config),
            VariableMinLength(config),
            VariableNaming(config),
            NonBooleanPropertyPrefixedWithIs(config)
        )
    )
}

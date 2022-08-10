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
            VariableNaming(config),
            TopLevelPropertyNaming(config),
            BooleanPropertyNaming(config),
            LambdaParameterNaming(config),
            FunctionParameterNaming(config),
            ConstructorParameterNaming(config),
            ForbiddenClassName(config),
            ClassNaming(config),
            PackageNaming(config),
            FunctionNaming(config),
            EnumNaming(config),
            ObjectPropertyNaming(config),
            FunctionMinLength(config),
            FunctionMaxLength(config),
            VariableMaxLength(config),
            VariableMaxLength(config),
        )
    )
}

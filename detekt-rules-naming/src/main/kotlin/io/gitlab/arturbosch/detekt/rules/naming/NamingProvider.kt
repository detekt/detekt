package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider

/**
 * The naming ruleset contains rules which assert the naming of different parts of the codebase.
 *
 * @active since v1.0.0
 */
class NamingProvider : DefaultRuleSetProvider {

    override val ruleSetId: String = "naming"

    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            MatchingDeclarationName(config),
            MemberNameEqualsClassName(config),
            NamingRules(config),
            InvalidPackageDeclaration(config)
        )
    )
}

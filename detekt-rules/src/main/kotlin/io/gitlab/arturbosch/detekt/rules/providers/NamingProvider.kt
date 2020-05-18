package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider
import io.gitlab.arturbosch.detekt.rules.naming.InvalidPackageDeclaration
import io.gitlab.arturbosch.detekt.rules.naming.MatchingDeclarationName
import io.gitlab.arturbosch.detekt.rules.naming.MemberNameEqualsClassName
import io.gitlab.arturbosch.detekt.rules.naming.NamingRules

/**
 * The naming ruleset contains rules which assert the naming of different parts of the codebase.
 *
 * @active since v1.0.0
 */
class NamingProvider : DefaultRuleSetProvider {

    override val ruleSetId: String = "naming"

    override fun instance(config: Config): RuleSet {
        return RuleSet(
            ruleSetId,
            listOf(
                MatchingDeclarationName(config),
                MemberNameEqualsClassName(config),
                NamingRules(config),
                InvalidPackageDeclaration(config)
            )
        )
    }
}

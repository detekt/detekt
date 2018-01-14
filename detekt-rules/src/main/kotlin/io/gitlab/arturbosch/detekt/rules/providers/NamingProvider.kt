package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.style.MatchingDeclarationName
import io.gitlab.arturbosch.detekt.rules.style.MemberNameEqualsClassName
import io.gitlab.arturbosch.detekt.rules.style.naming.NamingRules

/**
 * The naming ruleset contains rules which assert the naming of different parts of the codebase.
 *
 * @active since v1.0.0
 * @author Marvin Ramin
 */
class NamingProvider : RuleSetProvider {

	override val ruleSetId: String = "naming"

	override fun instance(config: Config): RuleSet {
		return RuleSet(ruleSetId, listOf(
				MatchingDeclarationName(config),
				MemberNameEqualsClassName(config),
				NamingRules(config)
		))
	}

}

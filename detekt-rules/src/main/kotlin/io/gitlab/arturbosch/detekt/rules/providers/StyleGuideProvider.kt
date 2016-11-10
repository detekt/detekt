package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.NamingConventionViolation
import io.gitlab.arturbosch.detekt.rules.OptionalSemicolon
import io.gitlab.arturbosch.detekt.rules.OptionalUnit
import io.gitlab.arturbosch.detekt.rules.WildcardImport

/**
 * @author Artur Bosch
 */
class StyleGuideProvider : RuleSetProvider {

	override val ruleSetId: String = "style"

	override fun instance(config: Config): RuleSet {
		return RuleSet(ruleSetId, listOf(
				WildcardImport(config),
				OptionalSemicolon(config),
				OptionalUnit(config),
				NamingConventionViolation(config)
		))
	}
}
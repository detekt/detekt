package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.style.MaxLineLength
import io.gitlab.arturbosch.detekt.rules.style.NamingConventionViolation
import io.gitlab.arturbosch.detekt.rules.style.TodoComment
import io.gitlab.arturbosch.detekt.rules.style.WildcardImport

/**
 * @author Artur Bosch
 */
class StyleGuideProvider : RuleSetProvider {

	override val ruleSetId: String = "style"

	override fun instance(config: Config): RuleSet {
		return RuleSet(ruleSetId, listOf(
				WildcardImport(config),
				MaxLineLength(config),
				TodoComment(config),
				NamingConventionViolation(config)
		))
	}
}

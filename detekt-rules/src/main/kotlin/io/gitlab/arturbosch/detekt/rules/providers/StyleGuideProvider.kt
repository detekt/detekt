package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.style.FileParsingRule
import io.gitlab.arturbosch.detekt.rules.style.ForbiddenComment
import io.gitlab.arturbosch.detekt.rules.style.ForbiddenImport
import io.gitlab.arturbosch.detekt.rules.style.ModifierOrder
import io.gitlab.arturbosch.detekt.rules.style.NamingConventionViolation
import io.gitlab.arturbosch.detekt.rules.style.NewLineAtEndOfFile
import io.gitlab.arturbosch.detekt.rules.style.OptionalAbstractKeyword
import io.gitlab.arturbosch.detekt.rules.style.ReturnCount
import io.gitlab.arturbosch.detekt.rules.style.SafeCast
import io.gitlab.arturbosch.detekt.rules.style.WildcardImport

/**
 * @author Artur Bosch
 */
class StyleGuideProvider : RuleSetProvider {

	override val ruleSetId: String = "style"

	override fun instance(config: Config): RuleSet {
		return RuleSet(ruleSetId, listOf(
				ReturnCount(config),
				NewLineAtEndOfFile(config),
				WildcardImport(config),
				FileParsingRule(config),
				ForbiddenComment(config),
				ForbiddenImport(config),
				NamingConventionViolation(config),
				SafeCast(config),
				OptionalAbstractKeyword(config),
				ModifierOrder(config)
		))
	}
}

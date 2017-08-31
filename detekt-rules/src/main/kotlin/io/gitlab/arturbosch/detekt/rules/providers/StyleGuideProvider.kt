package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.style.DataClassContainsFunctionsRule
import io.gitlab.arturbosch.detekt.rules.style.EqualsNullCall
import io.gitlab.arturbosch.detekt.rules.style.FileParsingRule
import io.gitlab.arturbosch.detekt.rules.style.ForbiddenComment
import io.gitlab.arturbosch.detekt.rules.style.ForbiddenImport
import io.gitlab.arturbosch.detekt.rules.style.MagicNumber
import io.gitlab.arturbosch.detekt.rules.style.ModifierOrder
import io.gitlab.arturbosch.detekt.rules.style.NewLineAtEndOfFile
import io.gitlab.arturbosch.detekt.rules.style.OptionalAbstractKeyword
import io.gitlab.arturbosch.detekt.rules.style.OptionalWhenBraces
import io.gitlab.arturbosch.detekt.rules.style.ProtectedMemberInFinalClass
import io.gitlab.arturbosch.detekt.rules.style.ReturnCount
import io.gitlab.arturbosch.detekt.rules.style.SafeCast
import io.gitlab.arturbosch.detekt.rules.style.UnnecessaryParentheses
import io.gitlab.arturbosch.detekt.rules.style.WildcardImport
import io.gitlab.arturbosch.detekt.rules.style.naming.NamingRules

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
				EqualsNullCall(config),
				ForbiddenComment(config),
				ForbiddenImport(config),
				NamingRules(config),
				SafeCast(config),
				UnnecessaryParentheses(config),
				OptionalAbstractKeyword(config),
				OptionalWhenBraces(config),
				ProtectedMemberInFinalClass(config),
				MagicNumber(config),
				ModifierOrder(config),
				DataClassContainsFunctionsRule(config)
		))
	}
}

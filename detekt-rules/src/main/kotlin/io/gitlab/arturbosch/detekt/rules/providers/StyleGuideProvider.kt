package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.empty.EmptyDefaultConstructor
import io.gitlab.arturbosch.detekt.rules.empty.EmptyInitBlock
import io.gitlab.arturbosch.detekt.rules.empty.EmptySecondaryConstructor
import io.gitlab.arturbosch.detekt.rules.style.EqualsNullCall
import io.gitlab.arturbosch.detekt.rules.style.FileParsingRule
import io.gitlab.arturbosch.detekt.rules.style.ForbiddenComment
import io.gitlab.arturbosch.detekt.rules.style.ForbiddenImport
import io.gitlab.arturbosch.detekt.rules.style.MagicNumber
import io.gitlab.arturbosch.detekt.rules.style.ModifierOrder
import io.gitlab.arturbosch.detekt.rules.style.naming.NamingRules
import io.gitlab.arturbosch.detekt.rules.style.NewLineAtEndOfFile
import io.gitlab.arturbosch.detekt.rules.style.OptionalAbstractKeyword
import io.gitlab.arturbosch.detekt.rules.style.ProtectedMemberInFinalClass
import io.gitlab.arturbosch.detekt.rules.style.ReturnCount
import io.gitlab.arturbosch.detekt.rules.style.SafeCast
import io.gitlab.arturbosch.detekt.rules.style.UnnecessaryParentheses
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
				EqualsNullCall(config),
				ForbiddenComment(config),
				ForbiddenImport(config),
				NamingRules(config),
				SafeCast(config),
				UnnecessaryParentheses(config),
				OptionalAbstractKeyword(config),
				ProtectedMemberInFinalClass(config),
				MagicNumber(config),
				ModifierOrder(config),
				EmptyInitBlock(config),
				EmptyDefaultConstructor(config),
				EmptySecondaryConstructor(config)
		))
	}
}

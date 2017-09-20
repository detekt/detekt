package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.style.DataClassContainsFunctionsRule
import io.gitlab.arturbosch.detekt.rules.style.EqualsNullCall
import io.gitlab.arturbosch.detekt.rules.style.ExpressionBodySyntax
import io.gitlab.arturbosch.detekt.rules.style.FileParsingRule
import io.gitlab.arturbosch.detekt.rules.style.ForbiddenComment
import io.gitlab.arturbosch.detekt.rules.style.ForbiddenImport
import io.gitlab.arturbosch.detekt.rules.style.LoopWithTooManyJumpStatements
import io.gitlab.arturbosch.detekt.rules.style.MagicNumber
import io.gitlab.arturbosch.detekt.rules.style.ModifierOrder
import io.gitlab.arturbosch.detekt.rules.style.NewLineAtEndOfFile
import io.gitlab.arturbosch.detekt.rules.style.OptionalAbstractKeyword
import io.gitlab.arturbosch.detekt.rules.style.OptionalWhenBraces
import io.gitlab.arturbosch.detekt.rules.style.PackageDeclarationStyle
import io.gitlab.arturbosch.detekt.rules.style.ProtectedMemberInFinalClass
import io.gitlab.arturbosch.detekt.rules.style.ReturnCount
import io.gitlab.arturbosch.detekt.rules.style.SafeCast
import io.gitlab.arturbosch.detekt.rules.style.SerialVersionUIDInSerializableClass
import io.gitlab.arturbosch.detekt.rules.style.ThrowsCount
import io.gitlab.arturbosch.detekt.rules.style.UnnecessaryAbstractClass
import io.gitlab.arturbosch.detekt.rules.style.UnnecessaryInheritance
import io.gitlab.arturbosch.detekt.rules.style.UnnecessaryParentheses
import io.gitlab.arturbosch.detekt.rules.style.UnusedImports
import io.gitlab.arturbosch.detekt.rules.style.UseDataClass
import io.gitlab.arturbosch.detekt.rules.style.UtilityClassWithPublicConstructor
import io.gitlab.arturbosch.detekt.rules.style.WildcardImport
import io.gitlab.arturbosch.detekt.rules.style.naming.NamingRules
import io.gitlab.arturbosch.detekt.rules.style.optional.OptionalReturnKeyword
import io.gitlab.arturbosch.detekt.rules.style.optional.OptionalUnit

/**
 * @author Artur Bosch
 */
class StyleGuideProvider : RuleSetProvider {

	override val ruleSetId: String = "style"

	override fun instance(config: Config): RuleSet {
		return RuleSet(ruleSetId, listOf(
				ReturnCount(config),
				ThrowsCount(config),
				NewLineAtEndOfFile(config),
				WildcardImport(config),
				FileParsingRule(config),
				EqualsNullCall(config),
				ForbiddenComment(config),
				ForbiddenImport(config),
				PackageDeclarationStyle(config),
				LoopWithTooManyJumpStatements(config),
				NamingRules(config),
				SafeCast(config),
				UnnecessaryAbstractClass(config),
				UnnecessaryParentheses(config),
				UnnecessaryInheritance(config),
				UtilityClassWithPublicConstructor(config),
				OptionalAbstractKeyword(config),
				OptionalWhenBraces(config),
				OptionalReturnKeyword(config),
				OptionalUnit(config),
				ProtectedMemberInFinalClass(config),
				SerialVersionUIDInSerializableClass(config),
				MagicNumber(config),
				ModifierOrder(config),
				DataClassContainsFunctionsRule(config),
				UseDataClass(config),
				UnusedImports(config),
				ExpressionBodySyntax(config)
		))
	}
}

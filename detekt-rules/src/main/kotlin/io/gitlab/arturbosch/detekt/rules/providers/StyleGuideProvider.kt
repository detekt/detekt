package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.style.CollapsibleIfStatements
import io.gitlab.arturbosch.detekt.rules.style.DataClassContainsFunctions
import io.gitlab.arturbosch.detekt.rules.style.DataClassShouldBeImmutable
import io.gitlab.arturbosch.detekt.rules.style.EqualsNullCall
import io.gitlab.arturbosch.detekt.rules.style.EqualsOnSignatureLine
import io.gitlab.arturbosch.detekt.rules.style.ExplicitItLambdaParameter
import io.gitlab.arturbosch.detekt.rules.style.ExpressionBodySyntax
import io.gitlab.arturbosch.detekt.rules.style.FileParsingRule
import io.gitlab.arturbosch.detekt.rules.style.ForbiddenComment
import io.gitlab.arturbosch.detekt.rules.style.ForbiddenImport
import io.gitlab.arturbosch.detekt.rules.style.ForbiddenVoid
import io.gitlab.arturbosch.detekt.rules.style.FunctionOnlyReturningConstant
import io.gitlab.arturbosch.detekt.rules.style.LibraryCodeMustSpecifyReturnType
import io.gitlab.arturbosch.detekt.rules.style.LoopWithTooManyJumpStatements
import io.gitlab.arturbosch.detekt.rules.style.MagicNumber
import io.gitlab.arturbosch.detekt.rules.style.MayBeConst
import io.gitlab.arturbosch.detekt.rules.style.ModifierOrder
import io.gitlab.arturbosch.detekt.rules.style.NestedClassesVisibility
import io.gitlab.arturbosch.detekt.rules.style.NewLineAtEndOfFile
import io.gitlab.arturbosch.detekt.rules.style.OptionalAbstractKeyword
import io.gitlab.arturbosch.detekt.rules.style.OptionalWhenBraces
import io.gitlab.arturbosch.detekt.rules.style.ProtectedMemberInFinalClass
import io.gitlab.arturbosch.detekt.rules.style.RedundantVisibilityModifierRule
import io.gitlab.arturbosch.detekt.rules.style.ReturnCount
import io.gitlab.arturbosch.detekt.rules.style.SafeCast
import io.gitlab.arturbosch.detekt.rules.style.SerialVersionUIDInSerializableClass
import io.gitlab.arturbosch.detekt.rules.style.SpacingBetweenPackageAndImports
import io.gitlab.arturbosch.detekt.rules.style.ThrowsCount
import io.gitlab.arturbosch.detekt.rules.style.UnderscoresInNumericLiterals
import io.gitlab.arturbosch.detekt.rules.style.UnnecessaryAbstractClass
import io.gitlab.arturbosch.detekt.rules.style.UnnecessaryApply
import io.gitlab.arturbosch.detekt.rules.style.UnnecessaryInheritance
import io.gitlab.arturbosch.detekt.rules.style.UnnecessaryLet
import io.gitlab.arturbosch.detekt.rules.style.UnnecessaryParentheses
import io.gitlab.arturbosch.detekt.rules.style.UntilInsteadOfRangeTo
import io.gitlab.arturbosch.detekt.rules.style.UnusedImports
import io.gitlab.arturbosch.detekt.rules.style.UnusedPrivateClass
import io.gitlab.arturbosch.detekt.rules.style.UnusedPrivateMember
import io.gitlab.arturbosch.detekt.rules.style.UseCheckOrError
import io.gitlab.arturbosch.detekt.rules.style.UseDataClass
import io.gitlab.arturbosch.detekt.rules.style.UseRequire
import io.gitlab.arturbosch.detekt.rules.style.UselessCallOnNotNull
import io.gitlab.arturbosch.detekt.rules.style.UtilityClassWithPublicConstructor
import io.gitlab.arturbosch.detekt.rules.style.VarCouldBeVal
import io.gitlab.arturbosch.detekt.rules.style.WildcardImport
import io.gitlab.arturbosch.detekt.rules.style.optional.MandatoryBracesIfStatements
import io.gitlab.arturbosch.detekt.rules.style.optional.OptionalUnit
import io.gitlab.arturbosch.detekt.rules.style.optional.PreferToOverPairSyntax

/**
 * The Style ruleset provides rules that assert the style of the code.
 * This will help keep code in line with the given
 * code style guidelines.
 *
 * @active since v1.0.0
 * @author Artur Bosch
 */
class StyleGuideProvider : RuleSetProvider {

    override val ruleSetId: String = "style"

    override fun instance(config: Config): RuleSet {
        return RuleSet(
            ruleSetId,
            listOf(
                CollapsibleIfStatements(config),
                ReturnCount(config),
                ThrowsCount(config),
                NewLineAtEndOfFile(config),
                WildcardImport(config),
                FileParsingRule(config),
                EqualsOnSignatureLine(config),
                EqualsNullCall(config),
                ForbiddenComment(config),
                ForbiddenImport(config),
                FunctionOnlyReturningConstant(config),
                SpacingBetweenPackageAndImports(config),
                LoopWithTooManyJumpStatements(config),
                SafeCast(config),
                UnnecessaryAbstractClass(config),
                UnnecessaryParentheses(config),
                UnnecessaryInheritance(config),
                UtilityClassWithPublicConstructor(config),
                OptionalAbstractKeyword(config),
                OptionalWhenBraces(config),
                OptionalUnit(config),
                ProtectedMemberInFinalClass(config),
                SerialVersionUIDInSerializableClass(config),
                MagicNumber(config),
                ModifierOrder(config),
                DataClassContainsFunctions(config),
                DataClassShouldBeImmutable(config),
                UseDataClass(config),
                UnusedImports(config),
                UnusedPrivateClass(config),
                UnusedPrivateMember(config),
                ExpressionBodySyntax(config),
                NestedClassesVisibility(config),
                RedundantVisibilityModifierRule(config),
                UntilInsteadOfRangeTo(config),
                UnnecessaryApply(config),
                UnnecessaryLet(config),
                MayBeConst(config),
                PreferToOverPairSyntax(config),
                MandatoryBracesIfStatements(config),
                VarCouldBeVal(config),
                ForbiddenVoid(config),
                ExplicitItLambdaParameter(config),
                UselessCallOnNotNull(config),
                UnderscoresInNumericLiterals(config),
                UseRequire(config),
                UseCheckOrError(config),
                LibraryCodeMustSpecifyReturnType(config)
            )
        )
    }
}

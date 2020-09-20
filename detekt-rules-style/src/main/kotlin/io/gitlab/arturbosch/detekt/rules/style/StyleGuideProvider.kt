package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider
import io.gitlab.arturbosch.detekt.rules.style.optional.MandatoryBracesIfStatements
import io.gitlab.arturbosch.detekt.rules.style.optional.MandatoryBracesLoops
import io.gitlab.arturbosch.detekt.rules.style.optional.OptionalUnit
import io.gitlab.arturbosch.detekt.rules.style.optional.PreferToOverPairSyntax

/**
 * The Style ruleset provides rules that assert the style of the code.
 * This will help keep code in line with the given
 * code style guidelines.
 *
 * @active since v1.0.0
 */
class StyleGuideProvider : DefaultRuleSetProvider {

    override val ruleSetId: String = "style"

    @Suppress("LongMethod")
    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            ClassOrdering(config),
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
            ForbiddenMethodCall(config),
            ForbiddenPublicDataClass(config),
            FunctionOnlyReturningConstant(config),
            SpacingBetweenPackageAndImports(config),
            LoopWithTooManyJumpStatements(config),
            SafeCast(config),
            UnnecessaryAbstractClass(config),
            UnnecessaryAnnotationUseSiteTarget(config),
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
            MandatoryBracesLoops(config),
            VarCouldBeVal(config),
            ForbiddenVoid(config),
            ExplicitItLambdaParameter(config),
            ExplicitCollectionElementAccessMethod(config),
            UselessCallOnNotNull(config),
            UnderscoresInNumericLiterals(config),
            UseRequire(config),
            UseCheckOrError(config),
            UseIfInsteadOfWhen(config),
            RedundantExplicitType(config),
            LibraryEntitiesShouldNotBePublic(config),
            LibraryCodeMustSpecifyReturnType(config),
            UseArrayLiteralsInAnnotations(config),
            UseEmptyCounterpart(config),
            UseCheckNotNull(config),
            UseRequireNotNull(config)
        )
    )
}

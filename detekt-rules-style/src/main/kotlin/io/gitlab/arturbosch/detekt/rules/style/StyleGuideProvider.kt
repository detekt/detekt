package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider
import io.gitlab.arturbosch.detekt.rules.style.optional.MandatoryBracesIfStatements
import io.gitlab.arturbosch.detekt.rules.style.optional.MandatoryBracesLoops
import io.gitlab.arturbosch.detekt.rules.style.optional.OptionalUnit
import io.gitlab.arturbosch.detekt.rules.style.optional.PreferToOverPairSyntax

/**
 * The Style ruleset provides rules that assert the style of the code.
 * This will help keep code in line with the given
 * code style guidelines.
 */
@ActiveByDefault(since = "1.0.0")
class StyleGuideProvider : DefaultRuleSetProvider {

    override val ruleSetId: String = "style"

    @Suppress("LongMethod")
    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            CanBeNonNullable(config),
            ClassOrdering(config),
            CollapsibleIfStatements(config),
            DataClassContainsFunctions(config),
            DataClassShouldBeImmutable(config),
            DestructuringDeclarationWithTooManyEntries(config),
            EqualsNullCall(config),
            EqualsOnSignatureLine(config),
            ExplicitCollectionElementAccessMethod(config),
            ExplicitItLambdaParameter(config),
            ExpressionBodySyntax(config),
            FileParsingRule(config),
            ForbiddenComment(config),
            ForbiddenImport(config),
            ForbiddenMethodCall(config),
            ForbiddenPublicDataClass(config),
            ForbiddenSuppress(config),
            ForbiddenVoid(config),
            FunctionOnlyReturningConstant(config),
            LibraryCodeMustSpecifyReturnType(config),
            LibraryEntitiesShouldNotBePublic(config),
            LoopWithTooManyJumpStatements(config),
            MagicNumber(config),
            MandatoryBracesIfStatements(config),
            MandatoryBracesLoops(config),
            MayBeConst(config),
            ModifierOrder(config),
            MultilineLambdaItParameter(config),
            NestedClassesVisibility(config),
            NestedLambdaOnSameLine(config),
            NewLineAtEndOfFile(config),
            NullableBooleanCheck(config),
            ObjectLiteralToLambda(config),
            OptionalAbstractKeyword(config),
            OptionalUnit(config),
            OptionalWhenBraces(config),
            PreferToOverPairSyntax(config),
            ProtectedMemberInFinalClass(config),
            RedundantExplicitType(config),
            RedundantHigherOrderMapUsage(config),
            RedundantVisibilityModifierRule(config),
            ReturnCount(config),
            SafeCast(config),
            SerialVersionUIDInSerializableClass(config),
            SpacingBetweenPackageAndImports(config),
            ThrowsCount(config),
            UnderscoresInNumericLiterals(config),
            UnnecessaryAbstractClass(config),
            UnnecessaryAnnotationUseSiteTarget(config),
            UnnecessaryApply(config),
            UnnecessaryBackticks(config),
            UnnecessaryFilter(config),
            UnnecessaryInheritance(config),
            UnnecessaryInnerClass(config),
            UnnecessaryLet(config),
            UnnecessaryParentheses(config),
            UntilInsteadOfRangeTo(config),
            UnusedImports(config),
            UnusedPrivateClass(config),
            UnusedPrivateMember(config),
            UseAnyOrNoneInsteadOfFind(config),
            UseArrayLiteralsInAnnotations(config),
            UseCheckNotNull(config),
            UseCheckOrError(config),
            UseDataClass(config),
            UseEmptyCounterpart(config),
            UseIfEmptyOrIfBlank(config),
            UseIfInsteadOfWhen(config),
            UseIsNullOrEmpty(config),
            UseOrEmpty(config),
            UseRequire(config),
            UseRequireNotNull(config),
            UselessCallOnNotNull(config),
            UtilityClassWithPublicConstructor(config),
            VarCouldBeVal(config),
            WildcardImport(config),
        )
    )
}

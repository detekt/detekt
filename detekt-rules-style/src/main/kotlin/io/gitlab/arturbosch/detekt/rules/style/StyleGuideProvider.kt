package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider
import io.gitlab.arturbosch.detekt.rules.style.movelambdaout.UnnecessaryBracesAroundTrailingLambda
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

    override val ruleSetId = RuleSet.Id("style")

    @Suppress("LongMethod")
    override fun instance(): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            ::CanBeNonNullable,
            ::CascadingCallWrapping,
            ::ClassOrdering,
            ::CollapsibleIfStatements,
            ::DestructuringDeclarationWithTooManyEntries,
            ::ReturnCount,
            ::ThrowsCount,
            ::TrimMultilineRawString,
            ::NewLineAtEndOfFile,
            ::WildcardImport,
            ::MaxLineLength,
            ::TrailingWhitespace,
            ::NoTabs,
            ::EqualsOnSignatureLine,
            ::EqualsNullCall,
            ::ForbiddenAnnotation,
            ::ForbiddenComment,
            ::ForbiddenImport,
            ::ForbiddenMethodCall,
            ::ForbiddenNamedParam,
            ::ForbiddenSuppress,
            ::FunctionOnlyReturningConstant,
            ::SpacingAfterPackageDeclaration,
            ::LoopWithTooManyJumpStatements,
            ::SafeCast,
            ::AbstractClassCanBeConcreteClass,
            ::AbstractClassCanBeInterface,
            ::UnnecessaryAnnotationUseSiteTarget,
            ::UnnecessaryParentheses,
            ::UnnecessaryInheritance,
            ::UnnecessaryInnerClass,
            ::UtilityClassWithPublicConstructor,
            ::ObjectLiteralToLambda,
            ::OptionalAbstractKeyword,
            ::OptionalUnit,
            ::ProtectedMemberInFinalClass,
            ::SerialVersionUIDInSerializableClass,
            ::MagicNumber,
            ::ModifierOrder,
            ::DataClassContainsFunctions,
            ::DataClassShouldBeImmutable,
            ::UseDataClass,
            ::UnusedImport,
            ::UnusedParameter,
            ::UnusedPrivateClass,
            ::UnusedPrivateFunction,
            ::UnusedPrivateProperty,
            ::UnusedVariable,
            ::ExpressionBodySyntax,
            ::NestedClassesVisibility,
            ::RedundantVisibilityModifier,
            ::RangeUntilInsteadOfRangeTo,
            ::UnnecessaryApply,
            ::UnnecessaryAny,
            ::UnnecessaryBracesAroundTrailingLambda,
            ::UnnecessaryFilter,
            ::UnnecessaryLet,
            ::MayBeConstant,
            ::PreferToOverPairSyntax,
            ::BracesOnIfStatements,
            ::BracesOnWhenStatements,
            ::MandatoryBracesLoops,
            ::NullableBooleanCheck,
            ::VarCouldBeVal,
            ::ForbiddenVoid,
            ::ExplicitItLambdaParameter,
            ::ExplicitCollectionElementAccessMethod,
            ::UselessCallOnNotNull,
            ::UnderscoresInNumericLiterals,
            ::UseRequire,
            ::UseCheckOrError,
            ::UseIfInsteadOfWhen,
            ::RedundantConstructorKeyword,
            ::RedundantExplicitType,
            ::UseArrayLiteralsInAnnotations,
            ::UseEmptyCounterpart,
            ::UseCheckNotNull,
            ::UseRequireNotNull,
            ::RedundantHigherOrderMapUsage,
            ::UseIfEmptyOrIfBlank,
            ::MultilineLambdaItParameter,
            ::MultilineRawStringIndentation,
            ::StringShouldBeRawString,
            ::UseIsNullOrEmpty,
            ::UseOrEmpty,
            ::UseAnyOrNoneInsteadOfFind,
            ::UnnecessaryBackticks,
            ::MaxChainedCallsOnSameLine,
            ::AlsoCouldBeApply,
            ::UseSumOfInsteadOfFlatMapSize,
            ::DoubleNegativeLambda,
            ::UseLet,
            ::DoubleNegativeExpression,
            ::UnnecessaryReversed,
        )
    )
}

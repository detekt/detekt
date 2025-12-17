package dev.detekt.rules.style

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.internal.DefaultRuleSetProvider
import dev.detekt.rules.style.movelambdaout.UnnecessaryBracesAroundTrailingLambda
import dev.detekt.rules.style.optional.MandatoryBracesLoops
import dev.detekt.rules.style.optional.OptionalUnit

/**
 * The Style ruleset provides rules that assert the style of the code.
 * This will help keep code in line with the given
 * code style guidelines.
 */
@ActiveByDefault(since = "1.0.0")
class StyleGuideProvider : DefaultRuleSetProvider {

    override val ruleSetId = RuleSetId("style")

    @Suppress("LongMethod")
    override fun instance(): RuleSet =
        RuleSet(
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
                ::ForbiddenOptIn,
                ::ForbiddenSuppress,
                ::FunctionOnlyReturningConstant,
                ::SpacingAfterPackageDeclaration,
                ::LoopWithTooManyJumpStatements,
                ::SafeCast,
                ::AbstractClassCanBeConcreteClass,
                ::AbstractClassCanBeInterface,
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
                ::BracesOnIfStatements,
                ::BracesOnWhenStatements,
                ::MandatoryBracesLoops,
                ::NullableBooleanCheck,
                ::VarCouldBeVal,
                ::ForbiddenVoid,
                ::ExplicitItLambdaMultipleParameters,
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
                ::UnnecessaryFullyQualifiedName,
                )
        )
}

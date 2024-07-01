package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider

/**
 * The potential-bugs rule set provides rules that detect potential bugs.
 */
@ActiveByDefault(since = "1.0.0")
class PotentialBugProvider : DefaultRuleSetProvider {

    override val ruleSetId = RuleSet.Id("potential-bugs")

    override fun instance(): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            ::AvoidReferentialEquality,
            ::Deprecation,
            ::DontDowncastCollectionTypes,
            ::DoubleMutabilityForCollection,
            ::ElseCaseInsteadOfExhaustiveWhen,
            ::EqualsAlwaysReturnsTrueOrFalse,
            ::EqualsWithHashCodeExist,
            ::ExitOutsideMain,
            ::ExplicitGarbageCollectionCall,
            ::HasPlatformType,
            ::ImplicitDefaultLocale,
            ::InvalidRange,
            ::IteratorHasNextCallsNextMethod,
            ::IteratorNotThrowingNoSuchElementException,
            ::LateinitUsage,
            ::MapGetWithNotNullAssertionOperator,
            ::MissingPackageDeclaration,
            ::MissingUseCall,
            ::NullCheckOnMutableProperty,
            ::UnconditionalJumpStatementInLoop,
            ::UnnamedParameterUse,
            ::UnnecessaryNotNullOperator,
            ::UnnecessaryNotNullCheck,
            ::UnnecessarySafeCall,
            ::UnreachableCode,
            ::UnsafeCallOnNullableType,
            ::UnsafeCast,
            ::UselessPostfixExpression,
            ::WrongEqualsTypeParameter,
            ::IgnoredReturnValue,
            ::ImplicitUnitReturnType,
            ::NullableToStringCall,
            ::UnreachableCatchBlock,
            ::CastToNullableType,
            ::CastNullableToNonNullableType,
            ::UnusedUnaryOperator,
            ::PropertyUsedBeforeDeclaration,
            ::CharArrayToStringCall,
            ::MissingSuperCall,
        )
    )
}

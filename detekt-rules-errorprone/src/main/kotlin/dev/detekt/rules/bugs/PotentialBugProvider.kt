package dev.detekt.rules.bugs

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.internal.DefaultRuleSetProvider

/**
 * The potential-bugs rule set provides rules that detect potential bugs.
 */
@ActiveByDefault(since = "1.0.0")
class PotentialBugProvider : DefaultRuleSetProvider {

    override val ruleSetId = RuleSetId("potential-bugs")

    override fun instance(): RuleSet =
        RuleSet(
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

package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider

/**
 * The potential-bugs rule set provides rules that detect potential bugs.
 */
@ActiveByDefault(since = "1.0.0")
class PotentialBugProvider : DefaultRuleSetProvider {

    override val ruleSetId: String = "potential-bugs"

    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            AvoidReferentialEquality(config),
            Deprecation(config),
            DontDowncastCollectionTypes(config),
            DoubleMutabilityForCollection(config),
            @Suppress("DEPRECATION") DuplicateCaseInWhenExpression(config),
            @Suppress("DEPRECATION") ElseCaseInsteadOfExhaustiveWhen(config),
            EqualsAlwaysReturnsTrueOrFalse(config),
            EqualsWithHashCodeExist(config),
            ExitOutsideMain(config),
            ExplicitGarbageCollectionCall(config),
            HasPlatformType(config),
            ImplicitDefaultLocale(config),
            InvalidRange(config),
            IteratorHasNextCallsNextMethod(config),
            IteratorNotThrowingNoSuchElementException(config),
            LateinitUsage(config),
            MapGetWithNotNullAssertionOperator(config),
            MissingPackageDeclaration(config),
            @Suppress("DEPRECATION") MissingWhenCase(config),
            NullCheckOnMutableProperty(config),
            @Suppress("DEPRECATION") RedundantElseInWhen(config),
            UnconditionalJumpStatementInLoop(config),
            UnnecessaryNotNullOperator(config),
            UnnecessarySafeCall(config),
            UnreachableCode(config),
            UnsafeCallOnNullableType(config),
            UnsafeCast(config),
            UselessPostfixExpression(config),
            WrongEqualsTypeParameter(config),
            IgnoredReturnValue(config),
            ImplicitUnitReturnType(config),
            NullableToStringCall(config),
            UnreachableCatchBlock(config),
            CastToNullableType(config),
            UnusedUnaryOperator(config)
        )
    )
}

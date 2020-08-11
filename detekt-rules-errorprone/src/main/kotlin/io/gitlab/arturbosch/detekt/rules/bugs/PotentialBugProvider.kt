package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider

/**
 * The potential-bugs rule set provides rules that detect potential bugs.
 *
 * @active since v1.0.0
 */
class PotentialBugProvider : DefaultRuleSetProvider {

    override val ruleSetId: String = "potential-bugs"

    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            Deprecation(config),
            DuplicateCaseInWhenExpression(config),
            EqualsAlwaysReturnsTrueOrFalse(config),
            EqualsWithHashCodeExist(config),
            ExplicitGarbageCollectionCall(config),
            HasPlatformType(config),
            ImplicitDefaultLocale(config),
            InvalidRange(config),
            IteratorHasNextCallsNextMethod(config),
            IteratorNotThrowingNoSuchElementException(config),
            LateinitUsage(config),
            MapGetWithNotNullAssertionOperator(config),
            MissingWhenCase(config),
            RedundantElseInWhen(config),
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
            NullableToStringCall(config)
        )
    )
}

package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider
import io.gitlab.arturbosch.detekt.rules.bugs.Deprecation
import io.gitlab.arturbosch.detekt.rules.bugs.DuplicateCaseInWhenExpression
import io.gitlab.arturbosch.detekt.rules.bugs.EqualsAlwaysReturnsTrueOrFalse
import io.gitlab.arturbosch.detekt.rules.bugs.EqualsWithHashCodeExist
import io.gitlab.arturbosch.detekt.rules.bugs.ExplicitGarbageCollectionCall
import io.gitlab.arturbosch.detekt.rules.bugs.HasPlatformType
import io.gitlab.arturbosch.detekt.rules.bugs.ImplicitDefaultLocale
import io.gitlab.arturbosch.detekt.rules.bugs.InvalidRange
import io.gitlab.arturbosch.detekt.rules.bugs.IteratorHasNextCallsNextMethod
import io.gitlab.arturbosch.detekt.rules.bugs.IteratorNotThrowingNoSuchElementException
import io.gitlab.arturbosch.detekt.rules.bugs.LateinitUsage
import io.gitlab.arturbosch.detekt.rules.bugs.MapGetWithNotNullAssertionOperator
import io.gitlab.arturbosch.detekt.rules.bugs.MissingWhenCase
import io.gitlab.arturbosch.detekt.rules.bugs.RedundantElseInWhen
import io.gitlab.arturbosch.detekt.rules.bugs.UnconditionalJumpStatementInLoop
import io.gitlab.arturbosch.detekt.rules.bugs.UnreachableCode
import io.gitlab.arturbosch.detekt.rules.bugs.UnsafeCallOnNullableType
import io.gitlab.arturbosch.detekt.rules.bugs.UnnecessarySafeCall
import io.gitlab.arturbosch.detekt.rules.bugs.UnnecessaryNotNullOperator
import io.gitlab.arturbosch.detekt.rules.bugs.UnsafeCast
import io.gitlab.arturbosch.detekt.rules.bugs.UselessPostfixExpression
import io.gitlab.arturbosch.detekt.rules.bugs.WrongEqualsTypeParameter

/**
 * The potential-bugs rule set provides rules that detect potential bugs.
 *
 * @active since v1.0.0
 */
class PotentialBugProvider : DefaultRuleSetProvider {

    override val ruleSetId: String = "potential-bugs"

    override fun instance(config: Config): RuleSet {
        return RuleSet(
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
                WrongEqualsTypeParameter(config)
            )
        )
    }
}

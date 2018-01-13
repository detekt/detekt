package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.bugs.DuplicateCaseInWhenExpression
import io.gitlab.arturbosch.detekt.rules.bugs.EqualsAlwaysReturnsTrueOrFalse
import io.gitlab.arturbosch.detekt.rules.bugs.EqualsWithHashCodeExist
import io.gitlab.arturbosch.detekt.rules.bugs.ExplicitGarbageCollectionCall
import io.gitlab.arturbosch.detekt.rules.bugs.InvalidRange
import io.gitlab.arturbosch.detekt.rules.bugs.IteratorHasNextCallsNextMethod
import io.gitlab.arturbosch.detekt.rules.bugs.IteratorNotThrowingNoSuchElementException
import io.gitlab.arturbosch.detekt.rules.bugs.LateinitUsage
import io.gitlab.arturbosch.detekt.rules.bugs.UnconditionalJumpStatementInLoop
import io.gitlab.arturbosch.detekt.rules.bugs.UnreachableCode
import io.gitlab.arturbosch.detekt.rules.bugs.UnsafeCallOnNullableType
import io.gitlab.arturbosch.detekt.rules.bugs.UnsafeCast
import io.gitlab.arturbosch.detekt.rules.bugs.UselessPostfixExpression
import io.gitlab.arturbosch.detekt.rules.bugs.WrongEqualsTypeParameter

/**
 * The potential-bugs rule set provides rules that detect potential bugs.
 *
 * @active since v1.0.0
 * @author Artur Bosch
 */
class PotentialBugProvider : RuleSetProvider {

	override val ruleSetId: String = "potential-bugs"

	override fun instance(config: Config): RuleSet {
		return RuleSet(ruleSetId, listOf(
				DuplicateCaseInWhenExpression(config),
				EqualsAlwaysReturnsTrueOrFalse(config),
				EqualsWithHashCodeExist(config),
				IteratorNotThrowingNoSuchElementException(config),
				IteratorHasNextCallsNextMethod(config),
				UselessPostfixExpression(config),
				InvalidRange(config),
				WrongEqualsTypeParameter(config),
				ExplicitGarbageCollectionCall(config),
				LateinitUsage(config),
				UnconditionalJumpStatementInLoop(config),
				UnreachableCode(config),
				UnsafeCallOnNullableType(config),
				UnsafeCast(config)
		))
	}

}

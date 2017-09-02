package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.exceptions.InstanceOfCheckForException
import io.gitlab.arturbosch.detekt.rules.exceptions.IteratorNotThrowingNoSuchElementException
import io.gitlab.arturbosch.detekt.rules.exceptions.RethrowCaughtException
import io.gitlab.arturbosch.detekt.rules.exceptions.ReturnFromFinally
import io.gitlab.arturbosch.detekt.rules.exceptions.ThrowingExceptionFromFinally
import io.gitlab.arturbosch.detekt.rules.exceptions.ThrowingNewInstanceOfSameException
import io.gitlab.arturbosch.detekt.rules.exceptions.TooGenericExceptionCatched
import io.gitlab.arturbosch.detekt.rules.exceptions.TooGenericExceptionThrown

/**
 * @author Artur Bosch
 */
class ExceptionsProvider : RuleSetProvider {

	override val ruleSetId: String = "exceptions"

	override fun instance(config: Config): RuleSet {
		return RuleSet(ruleSetId, listOf(
				TooGenericExceptionCatched(config),
				TooGenericExceptionThrown(config),
				InstanceOfCheckForException(config),
				ReturnFromFinally(config),
				ThrowingExceptionFromFinally(config),
				RethrowCaughtException(config),
				ThrowingNewInstanceOfSameException(config),
				IteratorNotThrowingNoSuchElementException(config)
		))
	}

}

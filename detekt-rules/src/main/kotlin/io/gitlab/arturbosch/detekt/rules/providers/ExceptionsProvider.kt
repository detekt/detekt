package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.exceptions.CatchArrayIndexOutOfBoundsException
import io.gitlab.arturbosch.detekt.rules.exceptions.CatchError
import io.gitlab.arturbosch.detekt.rules.exceptions.CatchException
import io.gitlab.arturbosch.detekt.rules.exceptions.CatchIndexOutOfBoundsException
import io.gitlab.arturbosch.detekt.rules.exceptions.CatchNullPointerException
import io.gitlab.arturbosch.detekt.rules.exceptions.CatchRuntimeException
import io.gitlab.arturbosch.detekt.rules.exceptions.RethrowCaughtException
import io.gitlab.arturbosch.detekt.rules.exceptions.ThrowError
import io.gitlab.arturbosch.detekt.rules.exceptions.ThrowException
import io.gitlab.arturbosch.detekt.rules.exceptions.ThrowNullPointerException
import io.gitlab.arturbosch.detekt.rules.exceptions.ThrowRuntimeException
import io.gitlab.arturbosch.detekt.rules.exceptions.ThrowThrowable

/**
 * @author Artur Bosch
 */
class ExceptionsProvider : RuleSetProvider {

	override val ruleSetId: String = "exceptions"

	override fun instance(config: Config): RuleSet {
		return RuleSet(ruleSetId, listOf(
				CatchArrayIndexOutOfBoundsException(config),
				CatchIndexOutOfBoundsException(config),
				CatchRuntimeException(config),
				CatchError(config),
				CatchNullPointerException(config),
				CatchException(config),
				ThrowError(config),
				ThrowException(config),
				ThrowRuntimeException(config),
				ThrowNullPointerException(config),
				ThrowThrowable(config),
				RethrowCaughtException(config)
		))
	}

}

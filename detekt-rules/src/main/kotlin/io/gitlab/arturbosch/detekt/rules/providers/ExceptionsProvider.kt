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

/**
 * @author Artur Bosch
 */
class ExceptionsProvider : RuleSetProvider {

	override fun instance(config: Config): RuleSet {
		val providerId = "exceptions"
		val subConfig = config.subConfig(providerId)
		return RuleSet(providerId, listOf(
				CatchArrayIndexOutOfBoundsException(subConfig),
				CatchIndexOutOfBoundsException(subConfig),
				CatchRuntimeException(subConfig),
				CatchError(subConfig),
				CatchNullPointerException(subConfig),
				CatchException(subConfig)
		))
	}

}
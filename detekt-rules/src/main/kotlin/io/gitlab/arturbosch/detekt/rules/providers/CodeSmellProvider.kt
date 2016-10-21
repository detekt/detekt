package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.CommentOverPrivateMethod
import io.gitlab.arturbosch.detekt.rules.CommentOverPrivateProperty
import io.gitlab.arturbosch.detekt.rules.ComplexMethod
import io.gitlab.arturbosch.detekt.rules.LargeClass
import io.gitlab.arturbosch.detekt.rules.LongMethod
import io.gitlab.arturbosch.detekt.rules.LongParameterList

/**
 * @author Artur Bosch
 */
class CodeSmellProvider : RuleSetProvider {
	override fun instance(config: Config): RuleSet {
		val providerId = "code-smell"
		val subConfig = config.subConfig(providerId)
		return RuleSet(providerId, listOf(
				LongParameterList(subConfig),
				LongMethod(subConfig),
				LargeClass(subConfig),
				CommentOverPrivateMethod(config),
				CommentOverPrivateProperty(config),
				ComplexMethod(config)
		))
	}
}
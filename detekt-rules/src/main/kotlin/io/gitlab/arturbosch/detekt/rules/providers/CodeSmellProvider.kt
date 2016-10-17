package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.LargeClass
import io.gitlab.arturbosch.detekt.rules.LongMethod
import io.gitlab.arturbosch.detekt.rules.LongParameterList

/**
 * @author Artur Bosch
 */
class CodeSmellProvider : RuleSetProvider {
	override fun instance(): RuleSet {
		return RuleSet("code-smell", listOf(
				LongParameterList(),
				LongMethod(),
				LargeClass()
		))
	}
}
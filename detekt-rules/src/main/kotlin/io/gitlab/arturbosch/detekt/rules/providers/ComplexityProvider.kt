package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.complexity.ComplexCondition
import io.gitlab.arturbosch.detekt.rules.complexity.ComplexInterface
import io.gitlab.arturbosch.detekt.rules.complexity.ComplexMethod
import io.gitlab.arturbosch.detekt.rules.complexity.LabeledExpression
import io.gitlab.arturbosch.detekt.rules.complexity.LargeClass
import io.gitlab.arturbosch.detekt.rules.complexity.LongMethod
import io.gitlab.arturbosch.detekt.rules.complexity.LongParameterList
import io.gitlab.arturbosch.detekt.rules.complexity.MethodOverloading
import io.gitlab.arturbosch.detekt.rules.complexity.NestedBlockDepth
import io.gitlab.arturbosch.detekt.rules.complexity.StringLiteralDuplication
import io.gitlab.arturbosch.detekt.rules.complexity.TooManyFunctions

/**
 * This rule set contains rules that report complex code.
 *
 * @active since v1.0.0
 * @author Artur Bosch
 */
class ComplexityProvider : RuleSetProvider {

	override val ruleSetId: String = "complexity"

	override fun instance(config: Config): RuleSet {
		return RuleSet(ruleSetId, listOf(
				LongParameterList(config),
				LongMethod(config),
				LargeClass(config),
				ComplexInterface(config),
				ComplexMethod(config),
				StringLiteralDuplication(config),
				MethodOverloading(config),
				NestedBlockDepth(config),
				TooManyFunctions(config),
				ComplexCondition(config),
				LabeledExpression(config)
		))
	}
}

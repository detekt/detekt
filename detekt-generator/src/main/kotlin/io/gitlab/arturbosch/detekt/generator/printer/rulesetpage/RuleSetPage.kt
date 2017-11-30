package io.gitlab.arturbosch.detekt.generator.printer.rulesetpage

import io.gitlab.arturbosch.detekt.generator.collection.CodeExample
import io.gitlab.arturbosch.detekt.generator.collection.Rule
import io.gitlab.arturbosch.detekt.generator.collection.RuleSetProvider

data class RuleSetPage(
		val ruleSet: RuleSetProvider,
		val rules: List<RuleCode>
)

data class RuleCode(
		val rule: Rule,
		val codeExample: CodeExample?
)

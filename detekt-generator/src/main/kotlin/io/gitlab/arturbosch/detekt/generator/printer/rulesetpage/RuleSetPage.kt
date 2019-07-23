package io.gitlab.arturbosch.detekt.generator.printer.rulesetpage

import io.gitlab.arturbosch.detekt.generator.collection.Rule
import io.gitlab.arturbosch.detekt.generator.collection.RuleSetProvider

data class RuleSetPage(
    val ruleSet: RuleSetProvider,
    val rules: List<Rule>
)

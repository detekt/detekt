package io.gitlab.arturbosch.detekt.generator.collection

data class RuleSetPage(
    val ruleSet: RuleSetProvider,
    val rules: List<Rule>,
)

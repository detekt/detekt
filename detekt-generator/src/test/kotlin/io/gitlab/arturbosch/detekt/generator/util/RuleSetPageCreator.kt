package io.gitlab.arturbosch.detekt.generator.util

import io.gitlab.arturbosch.detekt.generator.collection.Configuration
import io.gitlab.arturbosch.detekt.generator.collection.Rule
import io.gitlab.arturbosch.detekt.generator.collection.RuleSetProvider
import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.RuleSetPage

internal fun createRuleSetPage(): RuleSetPage {
	val rules = createRules()
	val ruleSetProvider = RuleSetProvider("style", "style rule set", true, rules.map { it.name })
	return RuleSetPage(ruleSetProvider, rules)
}

internal fun createRules(): List<Rule> {
	val rule1 = Rule("WildcardImport", "a wildcard import", "import foo.*", "import foo.bar", true, "Defect",
			"10min", "", listOf(Configuration("conf1", "a config option", "foo")))
	val rule2 = Rule("EqualsNull", "equals null", "", "", false,"", "", "WildcardImport", emptyList())
	return listOf(rule1, rule2)
}

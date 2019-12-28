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
            "10min", "alias1, alias2", "", listOf(
                    Configuration("conf1", "a config option", "foo", null),
                    Configuration("conf2", "deprecated config", "false", "use conf1 instead")))
    val rule2 = Rule("EqualsNull", "equals null", "", "", false, "",
            "", null, "WildcardImport", emptyList())
    val rule3 = Rule("NoUnitKeyword", "removes :Unit", "fun stuff(): Unit {}",
            "fun stuff() {}", true, "", "5m", null, "", emptyList(), true)
    return listOf(rule1, rule2, rule3)
}

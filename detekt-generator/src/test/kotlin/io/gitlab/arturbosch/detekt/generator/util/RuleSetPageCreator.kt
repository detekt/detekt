package io.gitlab.arturbosch.detekt.generator.util

import io.gitlab.arturbosch.detekt.generator.collection.Active
import io.gitlab.arturbosch.detekt.generator.collection.Configuration
import io.gitlab.arturbosch.detekt.generator.collection.Inactive
import io.gitlab.arturbosch.detekt.generator.collection.Rule
import io.gitlab.arturbosch.detekt.generator.collection.RuleSetProvider
import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.RuleSetPage

internal fun createRuleSetPage(): RuleSetPage {
    val rules = createRules()
    val ruleSetProvider =
        RuleSetProvider(
            name = "style",
            description = "style rule set",
            defaultActivationStatus = Active("1.0.0"),
            rules = rules.map { it.name },
            configuration = listOf(
                Configuration(
                    name = "rulesetconfig1",
                    description = "description rulesetconfig1",
                    defaultValue = "true",
                    defaultAndroidValue = null,
                    deprecated = null
                ),
                Configuration(
                    name = "rulesetconfig2",
                    description = "description rulesetconfig2",
                    defaultValue = "['foo', 'bar']",
                    defaultAndroidValue = null,
                    deprecated = null
                ),
                Configuration(
                    name = "deprecatedSimpleConfig",
                    description = "description deprecatedSimpleConfig",
                    defaultValue = "true",
                    defaultAndroidValue = null,
                    deprecated = "is deprecated"
                ),
                Configuration(
                    name = "deprecatedListConfig",
                    description = "description deprecatedListConfig",
                    defaultValue = "['foo', 'bar']",
                    defaultAndroidValue = null,
                    deprecated = "is deprecated"
                ),
                Configuration(
                    name = "rulesetconfig3",
                    description = "description rulesetconfig2",
                    defaultValue = "['first', 'se*cond']",
                    defaultAndroidValue = null,
                    deprecated = null
                )
            )
        )
    return RuleSetPage(ruleSetProvider, rules)
}

internal fun createRules(): List<Rule> {
    val rule1 = Rule(
        name = "WildcardImport",
        description = "a wildcard import",
        nonCompliantCodeExample = "import foo.*",
        compliantCodeExample = "import foo.bar",
        defaultActivationStatus = Active(since = "1.0.0"),
        severity = "Defect",
        debt = "10min",
        aliases = "alias1, alias2",
        parent = "",
        configuration = listOf(
            Configuration("conf1", "a config option", "foo", null, null),
            Configuration("conf2", "deprecated config", "false", null, "use conf1 instead"),
            Configuration("conf3", "list config", "['a', 'b']", null, null),
            Configuration("conf4", "deprecated list config", "['a', 'b']", null, "use conf3 instead"),
            Configuration("conf5", "rule with android variants", "120", "100", null),
        )
    )
    val rule2 = Rule(
        name = "EqualsNull",
        description = "equals null",
        nonCompliantCodeExample = "",
        compliantCodeExample = "",
        defaultActivationStatus = Inactive,
        severity = "",
        debt = "",
        aliases = null,
        parent = "WildcardImport",
        configuration = emptyList()
    )
    val rule3 = Rule(
        name = "NoUnitKeyword",
        description = "removes :Unit",
        nonCompliantCodeExample = "fun stuff(): Unit {}",
        compliantCodeExample = "fun stuff() {}",
        defaultActivationStatus = Active(since = "1.16.0"),
        severity = "",
        debt = "5m",
        aliases = null,
        parent = "",
        configuration = emptyList(),
        autoCorrect = true,
        requiresTypeResolution = true
    )
    return listOf(rule1, rule2, rule3)
}

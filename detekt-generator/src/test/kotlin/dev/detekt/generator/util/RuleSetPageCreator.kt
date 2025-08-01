package dev.detekt.generator.util

import dev.detekt.generator.collection.Active
import dev.detekt.generator.collection.Configuration
import dev.detekt.generator.collection.DefaultValue.Companion.of
import dev.detekt.generator.collection.Inactive
import dev.detekt.generator.collection.Rule
import dev.detekt.generator.collection.RuleSetPage
import dev.detekt.generator.collection.RuleSetProvider

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
                    defaultValue = of(true),
                    defaultAndroidValue = null,
                    deprecated = null
                ),
                Configuration(
                    name = "rulesetconfig2",
                    description = "description rulesetconfig2",
                    defaultValue = of(listOf("foo", "bar")),
                    defaultAndroidValue = null,
                    deprecated = null
                ),
                Configuration(
                    name = "deprecatedSimpleConfig",
                    description = "description deprecatedSimpleConfig",
                    defaultValue = of(true),
                    defaultAndroidValue = null,
                    deprecated = "is deprecated"
                ),
                Configuration(
                    name = "deprecatedListConfig",
                    description = "description deprecatedListConfig",
                    defaultValue = of(listOf("foo", "bar")),
                    defaultAndroidValue = null,
                    deprecated = "is deprecated"
                ),
                Configuration(
                    name = "rulesetconfig3",
                    description = "description rulesetconfig2",
                    defaultValue = of(listOf("first", "se*cond")),
                    defaultAndroidValue = null,
                    deprecated = null
                )
            )
        )
    return RuleSetPage(ruleSetProvider, rules)
}

internal fun createRules(): List<Rule> {
    val rule1 = Rule(
        name = "MagicNumber",
        description = "a wildcard import",
        nonCompliantCodeExample = "import foo.*",
        compliantCodeExample = "import foo.bar",
        defaultActivationStatus = Active(since = "1.0.0"),
        aliases = listOf("alias1", "alias2"),
        parent = "",
        configurations = listOf(
            Configuration("conf1", "a config option", of("foo"), null, null),
            Configuration("conf2", "deprecated config", of(false), null, "use conf1 instead"),
            Configuration("conf3", "list config", of(listOf("a", "b")), null, null),
            Configuration("conf4", "deprecated list config", of(listOf("a", "b")), null, "use conf3 instead"),
            Configuration("conf5", "rule with android variants", of(120), of(100), null),
        )
    )
    val rule2 = Rule(
        name = "EqualsNull",
        description = "equals null",
        nonCompliantCodeExample = "",
        compliantCodeExample = "",
        defaultActivationStatus = Inactive,
        aliases = emptyList(),
        parent = "WildcardImport",
        configurations = emptyList()
    )
    val rule3 = Rule(
        name = "NoUnitKeyword",
        description = "removes :Unit",
        nonCompliantCodeExample = "fun stuff(): Unit {}",
        compliantCodeExample = "fun stuff() {}",
        defaultActivationStatus = Active(since = "1.16.0"),
        aliases = emptyList(),
        parent = "",
        configurations = emptyList(),
        autoCorrect = true,
        requiresFullAnalysis = true
    )
    val rule4 = Rule(
        name = "DuplicateCaseInWhenExpression",
        description = "Duplicated `case` statements in a `when` expression detected.",
        nonCompliantCodeExample = "fun stuff(): Unit {}",
        compliantCodeExample = "fun stuff() {}",
        defaultActivationStatus = Active(since = "1.16.0"),
        aliases = emptyList(),
        parent = "",
        deprecationMessage = "is deprecated"
    )
    return listOf(rule1, rule2, rule3, rule4)
}

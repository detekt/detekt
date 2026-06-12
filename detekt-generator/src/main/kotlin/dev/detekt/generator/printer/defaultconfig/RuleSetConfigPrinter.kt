package dev.detekt.generator.printer.defaultconfig

import dev.detekt.api.Config
import dev.detekt.generator.collection.Configuration
import dev.detekt.generator.collection.Rule
import dev.detekt.generator.collection.RuleSetPage
import dev.detekt.generator.collection.RuleSetProvider
import dev.detekt.utils.YamlNode
import dev.detekt.utils.comment
import dev.detekt.utils.keyValue
import dev.detekt.utils.node

internal fun YamlNode.printRuleSetPage(ruleSetPage: RuleSetPage) {
    printRuleSet(ruleSetPage.ruleSet, ruleSetPage.rules)
}

internal fun YamlNode.printRuleSet(ruleSet: RuleSetProvider, rules: List<Rule>) {
    node(ruleSet.name) {
        keyValue { Config.ACTIVE_KEY to "${ruleSet.defaultActivationStatus.active}" }
        val ruleSetExclusion = exclusions.singleOrNull { ruleSet.name in it.ruleSets }
        if (ruleSetExclusion != null) {
            keyValue { Config.EXCLUDES_KEY to ruleSetExclusion.pattern }
        }

        ruleSet.configuration.forEach(::printConfiguration)

        rules.forEach(::printRule)

        emptyLine()
    }
}

internal fun YamlNode.printRule(rule: Rule) {
    if (rule.isDeprecated()) return

    node(rule.name) {
        keyValue { Config.ACTIVE_KEY to "${rule.defaultActivationStatus.active}" }
        if (rule.aliases.isNotEmpty()) {
            keyValue { Config.ALIASES_KEY to "[${rule.aliases.joinToString(separator = ", ") { "'$it'" }}]" }
        }
        if (rule.autoCorrect) {
            keyValue { Config.AUTO_CORRECT_KEY to "true" }
        }
        val ruleExclusion = exclusions.singleOrNull { it.isExcluded(rule) }
        if (ruleExclusion != null) {
            keyValue { Config.EXCLUDES_KEY to ruleExclusion.pattern }
        }
        val ignoreAnnotated = ignoreAnnotatedDefaults.firstNotNullOfOrNull { it.getAnnotations(rule) }
        if (ignoreAnnotated != null) {
            keyValue {
                Config.IGNORE_ANNOTATED_KEY to ignoreAnnotated.joinToString(prefix = "[", postfix = "]") { "'$it'" }
            }
        }
        rule.configurations.forEach(::printConfiguration)
    }
}

internal fun YamlNode.printConfiguration(configuration: Configuration) {
    if (configuration.isDeprecated()) return

    val hasDeviatingAndroidDefault = configuration.defaultAndroidValue != null &&
        configuration.defaultValue != configuration.defaultAndroidValue

    if (hasDeviatingAndroidDefault) {
        val description =
            "If the 'code_style' ruleset property is set to 'android', " +
                "the default is '${configuration.defaultAndroidValue.getPlainValue()}', " +
                "otherwise '${configuration.defaultValue.getPlainValue()}'."
        comment("${configuration.name}: $description")
    } else {
        configuration.defaultValue.printAsYaml(configuration.name, this)
    }
}

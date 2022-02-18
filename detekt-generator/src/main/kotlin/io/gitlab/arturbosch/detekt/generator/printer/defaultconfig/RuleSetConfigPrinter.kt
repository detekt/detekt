package io.gitlab.arturbosch.detekt.generator.printer.defaultconfig

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.generator.collection.Configuration
import io.gitlab.arturbosch.detekt.generator.collection.Rule
import io.gitlab.arturbosch.detekt.generator.collection.RuleSetPage
import io.gitlab.arturbosch.detekt.generator.collection.RuleSetProvider
import io.gitlab.arturbosch.detekt.generator.out.YamlNode
import io.gitlab.arturbosch.detekt.generator.out.keyValue
import io.gitlab.arturbosch.detekt.generator.out.list
import io.gitlab.arturbosch.detekt.generator.out.node

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
    node(rule.name) {
        keyValue { Config.ACTIVE_KEY to "${rule.defaultActivationStatus.active}" }
        if (rule.autoCorrect) {
            keyValue { Config.AUTO_CORRECT_KEY to "true" }
        }
        val ruleExclusion = exclusions.singleOrNull { it.isExcluded(rule) }
        if (ruleExclusion != null) {
            keyValue { Config.EXCLUDES_KEY to ruleExclusion.pattern }
        }
        rule.configuration.forEach(::printConfiguration)
    }
}

internal fun YamlNode.printConfiguration(configuration: Configuration) {
    if (configuration.isDeprecated()) return

    configuration.defaultValue.printAsYaml(configuration.name, this)
}

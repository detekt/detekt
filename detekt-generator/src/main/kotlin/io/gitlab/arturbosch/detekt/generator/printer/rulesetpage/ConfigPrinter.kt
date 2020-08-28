package io.gitlab.arturbosch.detekt.generator.printer.rulesetpage

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.generator.collection.Rule
import io.gitlab.arturbosch.detekt.generator.collection.RuleSetProvider
import io.gitlab.arturbosch.detekt.generator.out.YamlNode
import io.gitlab.arturbosch.detekt.generator.out.keyValue
import io.gitlab.arturbosch.detekt.generator.out.list
import io.gitlab.arturbosch.detekt.generator.out.node
import io.gitlab.arturbosch.detekt.generator.out.yaml
import io.gitlab.arturbosch.detekt.generator.printer.DocumentationPrinter
import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.TestExclusions.isExcludedInTests

object ConfigPrinter : DocumentationPrinter<List<RuleSetPage>> {

    override fun print(item: List<RuleSetPage>): String {
        return yaml {
            yaml { defaultBuildConfiguration() }
            emptyLine()
            yaml { defaultConfigConfiguration() }
            emptyLine()
            yaml { defaultProcessorsConfiguration() }
            emptyLine()
            yaml { defaultConsoleReportsConfiguration() }
            emptyLine()
            yaml { defaultOutputReportsConfiguration() }
            emptyLine()

            item.sortedBy { it.ruleSet.name }
                .forEach { printRuleSet(it.ruleSet, it.rules) }
        }
    }

    @Suppress("ComplexMethod") // preserving the declarative structure while building the dsl
    private fun YamlNode.printRuleSet(ruleSet: RuleSetProvider, rules: List<Rule>) {
        node(ruleSet.name) {
            keyValue { "active" to "${ruleSet.active}" }
            if (ruleSet.name in TestExclusions.ruleSets) {
                keyValue { Config.EXCLUDES_KEY to TestExclusions.pattern }
            }
            ruleSet.configuration
                .forEach { configuration ->
                if (configuration.defaultValue.isYamlList()) {
                    list(configuration.name, configuration.defaultValue.toList())
                } else {
                    keyValue { configuration.name to configuration.defaultValue }
                }
            }
            rules.forEach { rule ->
                node(rule.name) {
                    keyValue { "active" to "${rule.active}" }
                    if (rule.autoCorrect) {
                        keyValue { "autoCorrect" to "true" }
                    }
                    if (rule.isExcludedInTests()) {
                        keyValue { Config.EXCLUDES_KEY to TestExclusions.pattern }
                    }
                    rule.configuration
                        .forEach { configuration ->
                        if (configuration.defaultValue.isYamlList()) {
                            list(configuration.name, configuration.defaultValue.toList())
                        } else if (configuration.deprecated == null) {
                            keyValue { configuration.name to configuration.defaultValue }
                        }
                    }
                }
            }
            emptyLine()
        }
    }

    private fun defaultBuildConfiguration(): String = """
      build:
        maxIssues: 0
        excludeCorrectable: false
        weights:
          # complexity: 2
          # LongParameterList: 1
          # style: 1
          # comments: 1
    """.trimIndent()

    private fun defaultConfigConfiguration(): String = """
      config:
        validation: true
        warningsAsErrors: false
        # when writing own rules with new properties, exclude the property path e.g.: 'my_rule_set,.*>.*>[my_property]'
        excludes: ''
    """.trimIndent()

    private fun defaultProcessorsConfiguration(): String = """
      processors:
        active: true
        exclude:
          - 'DetektProgressListener'
        # - 'FunctionCountProcessor'
        # - 'PropertyCountProcessor'
        # - 'ClassCountProcessor'
        # - 'PackageCountProcessor'
        # - 'KtFileCountProcessor'
    """.trimIndent()

    private fun defaultConsoleReportsConfiguration(): String = """
      console-reports:
        active: true
        exclude:
           - 'ProjectStatisticsReport'
           - 'ComplexityReport'
           - 'NotificationReport'
        #  - 'FindingsReport'
           - 'FileBasedFindingsReport'
    """.trimIndent()

    private fun defaultOutputReportsConfiguration(): String = """
      output-reports:
        active: true
        exclude:
        # - 'TxtOutputReport'
        # - 'XmlOutputReport'
        # - 'HtmlOutputReport'
    """.trimIndent()

    private fun String.isYamlList() = trim().startsWith("-")

    private fun String.toList(): List<String> =
        split("\n")
            .map { it.replace("-", "") }
            .map { it.trim() }
}

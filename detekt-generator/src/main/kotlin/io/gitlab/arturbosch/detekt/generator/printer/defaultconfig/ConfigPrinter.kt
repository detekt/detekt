package io.gitlab.arturbosch.detekt.generator.printer.defaultconfig

import io.github.detekt.utils.yaml
import io.gitlab.arturbosch.detekt.generator.collection.RuleSetPage
import io.gitlab.arturbosch.detekt.generator.printer.DocumentationPrinter

object ConfigPrinter : DocumentationPrinter<List<RuleSetPage>> {

    override fun print(item: List<RuleSetPage>): String =
        yaml {
            yaml { defaultConfigConfiguration() }
            emptyLine()
            yaml { defaultProcessorsConfiguration() }
            emptyLine()
            yaml { defaultConsoleReportsConfiguration() }
            emptyLine()
            yaml { defaultOutputReportsConfiguration() }
            emptyLine()

            item.sortedBy { it.ruleSet.name }
                .forEach { printRuleSetPage(it) }
        }

    fun printCustomRuleConfig(item: List<RuleSetPage>): String =
        yaml {
            item.sortedBy { it.ruleSet.name }
                .forEach { printRuleSetPage(it) }
        }

    private fun defaultConfigConfiguration(): String = """
        config:
          validation: true
          warningsAsErrors: false
          checkExhaustiveness: false
          # when writing own rules with new properties, exclude the property path e.g.: ['my_rule_set', '.*>.*>[my_property]']
          excludes: []
    """.trimIndent()

    private fun defaultProcessorsConfiguration(): String = """
        processors:
          active: true
          exclude:
            - 'DetektProgressListener'
          # - 'KtFileCountProcessor'
          # - 'PackageCountProcessor'
          # - 'ClassCountProcessor'
          # - 'FunctionCountProcessor'
          # - 'PropertyCountProcessor'
          # - 'ProjectComplexityProcessor'
          # - 'ProjectCognitiveComplexityProcessor'
          # - 'ProjectLLOCProcessor'
          # - 'ProjectCLOCProcessor'
          # - 'ProjectLOCProcessor'
          # - 'ProjectSLOCProcessor'
          # - 'LicenseHeaderLoaderExtension'
    """.trimIndent()

    private fun defaultConsoleReportsConfiguration(): String = """
        console-reports:
          active: true
          exclude:
             - 'ProjectStatisticsReport'
             - 'ComplexityReport'
             - 'NotificationReport'
             - 'IssuesReport'
             - 'FileBasedIssuesReport'
          #  - 'LiteIssuesReport'
    """.trimIndent()

    private fun defaultOutputReportsConfiguration(): String = """
        output-reports:
          active: true
          exclude:
          # - 'XmlOutputReport'
          # - 'HtmlOutputReport'
          # - 'MdOutputReport'
          # - 'sarif'
    """.trimIndent()
}

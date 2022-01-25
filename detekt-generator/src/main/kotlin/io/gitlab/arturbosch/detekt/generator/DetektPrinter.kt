package io.gitlab.arturbosch.detekt.generator

import io.gitlab.arturbosch.detekt.generator.collection.RuleSetPage
import io.gitlab.arturbosch.detekt.generator.out.MarkdownWriter
import io.gitlab.arturbosch.detekt.generator.out.PropertiesWriter
import io.gitlab.arturbosch.detekt.generator.out.YamlWriter
import io.gitlab.arturbosch.detekt.generator.out.yaml
import io.gitlab.arturbosch.detekt.generator.printer.DeprecatedPrinter
import io.gitlab.arturbosch.detekt.generator.printer.RuleSetPagePrinter
import io.gitlab.arturbosch.detekt.generator.printer.defaultconfig.ConfigPrinter
import io.gitlab.arturbosch.detekt.generator.printer.defaultconfig.printRuleSetPage
import java.nio.file.Paths

class DetektPrinter(private val arguments: GeneratorArgs) {

    private val markdownWriter = MarkdownWriter(System.out)
    private val yamlWriter = YamlWriter(System.out)
    private val propertiesWriter = PropertiesWriter(System.out)

    fun print(pages: List<RuleSetPage>) {
        pages.forEach {
            markdownWriter.write(arguments.documentationPath, it.ruleSet.name) {
                jekyllHeader(it.ruleSet.name) + "\n" + RuleSetPagePrinter.print(it)
            }
        }
        yamlWriter.write(arguments.configPath, "default-detekt-config") {
            ConfigPrinter.print(pages.filterNot { it.ruleSet.name == "formatting" })
        }
        propertiesWriter.write(arguments.configPath, "deprecation") {
            DeprecatedPrinter.print(pages.filterNot { it.ruleSet.name == "formatting" })
        }
        yamlWriter.write(Paths.get("../detekt-formatting/src/main/resources/config"), "config") {
            yaml {
                printRuleSetPage(pages.first { it.ruleSet.name == "formatting" })
            }
        }
    }

    private fun jekyllHeader(ruleSet: String): String {
        check(ruleSet.length > 1) { "Rule set name must be not empty or less than two symbols." }
        return """
            |---
            |title: ${ruleSet[0].toUpperCase()}${ruleSet.substring(1)} Rule Set
            |sidebar: home_sidebar
            |keywords: rules, $ruleSet
            |permalink: $ruleSet.html
            |toc: true
            |folder: documentation
            |---
        """.trimMargin()
    }
}

package io.gitlab.arturbosch.detekt.generator.printer

import io.gitlab.arturbosch.detekt.generator.GeneratorArgs
import io.gitlab.arturbosch.detekt.generator.out.MarkdownWriter
import io.gitlab.arturbosch.detekt.generator.out.YamlWriter
import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.ConfigPrinter
import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.RuleSetPage
import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.RuleSetPagePrinter

class DetektPrinter(private val arguments: GeneratorArgs) {

    private val markdownWriter = MarkdownWriter()
    private val yamlWriter = YamlWriter()

    fun print(pages: List<RuleSetPage>) {
        pages.forEach {
            markdownWriter.write(arguments.documentationPath, it.ruleSet.name) {
                jekyllHeader(it.ruleSet.name) + "\n" + RuleSetPagePrinter.print(it)
            }
        }
        yamlWriter.write(arguments.configPath, "default-detekt-config") { ConfigPrinter.print(pages) }
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

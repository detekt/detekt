package dev.detekt.generator

import dev.detekt.generator.collection.RuleSetPage
import dev.detekt.generator.out.MarkdownWriter
import dev.detekt.generator.out.PropertiesWriter
import dev.detekt.generator.out.YamlWriter
import dev.detekt.generator.printer.DeprecatedPrinter
import dev.detekt.generator.printer.RuleSetPagePrinter
import dev.detekt.generator.printer.defaultconfig.ConfigPrinter
import java.nio.file.Path

class DetektPrinter(private val documentationPath: Path?, private val configPath: Path?) {

    private val markdownWriter = MarkdownWriter(System.out)
    private val yamlWriter = YamlWriter(System.out)
    private val propertiesWriter = PropertiesWriter(System.out)

    fun print(pages: List<RuleSetPage>) {
        if (documentationPath != null) {
            pages.forEach {
                markdownWriter.write(documentationPath, it.ruleSet.name) {
                    markdownHeader(it.ruleSet.name) + "\n" + RuleSetPagePrinter.print(it)
                }
            }
        }
        if (configPath != null) {
            yamlWriter.write(configPath, "config") { ConfigPrinter.print(pages) }
            propertiesWriter.write(configPath, "deprecation") { DeprecatedPrinter.print(pages) }
        }
    }

    fun printCustomRuleConfig(pages: List<RuleSetPage>, folder: Path) {
        yamlWriter.write(folder, "config") {
            ConfigPrinter.print(pages)
        }
    }

    private fun markdownHeader(ruleSetName: String): String =
        """
            ---
            title: ${ruleSetName[0].uppercaseChar()}${ruleSetName.substring(1)} Rule Set
            sidebar: home_sidebar
            keywords: [rules, $ruleSetName]
            permalink: $ruleSetName.html
            toc: true
            folder: documentation
            ---
        """.trimIndent()
}

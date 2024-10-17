package io.gitlab.arturbosch.detekt.generator

import io.github.detekt.utils.yaml
import io.gitlab.arturbosch.detekt.generator.collection.RuleSetPage
import io.gitlab.arturbosch.detekt.generator.out.MarkdownWriter
import io.gitlab.arturbosch.detekt.generator.out.PropertiesWriter
import io.gitlab.arturbosch.detekt.generator.out.YamlWriter
import io.gitlab.arturbosch.detekt.generator.printer.DeprecatedPrinter
import io.gitlab.arturbosch.detekt.generator.printer.RuleSetPagePrinter
import io.gitlab.arturbosch.detekt.generator.printer.defaultconfig.ConfigPrinter
import io.gitlab.arturbosch.detekt.generator.printer.defaultconfig.printRuleSetPage
import java.nio.file.Path
import kotlin.io.path.Path

class DetektPrinter(
    private val documentationPath: Path?,
    private val configPath: Path?,
) {

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
            yamlWriter.write(configPath, "default-detekt-config") {
                ConfigPrinter.print(
                    pages.filterNot { it.ruleSet.name in listOf("formatting", "libraries", "ruleauthors") }
                )
            }
            propertiesWriter.write(configPath, "deprecation") {
                // We intentionally not filter for "formatting" as we want to be able to deprecate
                // properties from that ruleset as well.
                DeprecatedPrinter.print(pages)
            }
        }
        yamlWriter.write(Path("../detekt-rules-libraries/src/main/resources/config"), "config") {
            yaml {
                printRuleSetPage(pages.first { it.ruleSet.name == "libraries" })
            }
        }
        yamlWriter.write(Path("../detekt-rules-ruleauthors/src/main/resources/config"), "config") {
            yaml {
                printRuleSetPage(pages.first { it.ruleSet.name == "ruleauthors" })
            }
        }
    }

    fun printCustomRuleConfig(pages: List<RuleSetPage>, folder: Path) {
        yamlWriter.write(folder, "config") {
            ConfigPrinter.printCustomRuleConfig(pages)
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

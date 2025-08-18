package dev.detekt.generator

import dev.detekt.generator.collection.RuleSetPage
import dev.detekt.generator.out.MarkdownWriter
import dev.detekt.generator.out.PropertiesWriter
import dev.detekt.generator.out.YamlWriter
import dev.detekt.generator.printer.DeprecatedPrinter
import dev.detekt.generator.printer.RuleSetPagePrinter
import dev.detekt.generator.printer.defaultconfig.ConfigPrinter
import dev.detekt.generator.printer.defaultconfig.printRuleSetPage
import dev.detekt.utils.yaml
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
        yamlWriter.write(Path("../detekt-rules-ktlint-wrapper/src/main/resources/config"), "config") {
            yaml {
                printRuleSetPage(pages.first { it.ruleSet.name == "formatting" })
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

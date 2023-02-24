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
import kotlin.io.path.Path

class DetektPrinter(private val arguments: GeneratorArgs) {

    private val markdownWriter = MarkdownWriter(System.out)
    private val yamlWriter = YamlWriter(System.out)
    private val propertiesWriter = PropertiesWriter(System.out)

    fun print(pages: List<RuleSetPage>) {
        pages.forEach {
            markdownWriter.write(arguments.documentationPath, it.ruleSet.name) {
                markdownHeader(it.ruleSet.name) + "\n" + RuleSetPagePrinter.print(it)
            }
        }
        yamlWriter.write(arguments.configPath, "default-detekt-config") {
            ConfigPrinter.print(
                pages.filterNot { it.ruleSet.name in listOf("formatting", "libraries", "ruleauthors") }
            )
        }
        propertiesWriter.write(arguments.configPath, "deprecation") {
            // We intentionally not filter for "formatting" as we want to be able to deprecate
            // properties from that ruleset as well.
            DeprecatedPrinter.print(pages)
        }
        yamlWriter.write(Path("../detekt-formatting/src/main/resources/config"), "config") {
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

    fun printCustomRuleConfig(pages: List<RuleSetPage>, folder: String) {
        yamlWriter.write(Path(folder), "config") {
            ConfigPrinter.printCustomRuleConfig(pages)
        }
    }

    private fun markdownHeader(ruleSet: String): String {
        check(ruleSet.length > 1) { "Rule set name must be not empty or less than two symbols." }
        return """
            ---
            title: ${ruleSet[0].uppercaseChar()}${ruleSet.substring(1)} Rule Set
            sidebar: home_sidebar
            keywords: [rules, $ruleSet]
            permalink: $ruleSet.html
            toc: true
            folder: documentation
            ---
        """.trimIndent()
    }
}

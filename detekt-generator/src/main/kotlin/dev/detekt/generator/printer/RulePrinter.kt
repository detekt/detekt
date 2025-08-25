package dev.detekt.generator.printer

import dev.detekt.generator.collection.Active
import dev.detekt.generator.collection.Rule
import dev.detekt.utils.MarkdownContent
import dev.detekt.utils.bold
import dev.detekt.utils.codeBlock
import dev.detekt.utils.crossOut
import dev.detekt.utils.h3
import dev.detekt.utils.h4
import dev.detekt.utils.markdown
import dev.detekt.utils.paragraph

internal object RulePrinter : DocumentationPrinter<Rule> {

    override fun print(item: Rule): String =
        markdown {
            if (item.isDeprecated()) {
                h3 { crossOut { item.name } }
                paragraph { item.deprecationMessage.orEmpty() }
            } else {
                h3 { item.name }
            }

            if (item.description.isNotEmpty()) {
                paragraph { item.description }
            } else {
                paragraph { "TODO: Specify description" }
            }

            paragraph {
                "${bold { "Active by default" }}: ${if (item.defaultActivationStatus.active) "Yes" else "No"}" +
                    ((item.defaultActivationStatus as? Active)?.let { " - Since v${it.since}" }.orEmpty())
            }

            if (item.requiresFullAnalysis) {
                paragraph {
                    bold { "Requires Type Resolution" }
                }
            }

            if (item.aliases.isNotEmpty()) {
                paragraph {
                    "${bold { "Aliases" }}: ${item.aliases.joinToString(", ")}"
                }
            }

            markdown { ConfigurationsPrinter.print(item.configurations) }

            printRuleCodeExamples(item)
        }

    private fun MarkdownContent.printRuleCodeExamples(rule: Rule) {
        if (rule.nonCompliantCodeExample.isNotEmpty()) {
            h4 { "Noncompliant Code:" }
            paragraph { codeBlock { rule.nonCompliantCodeExample } }
        }

        if (rule.compliantCodeExample.isNotEmpty()) {
            h4 { "Compliant Code:" }
            paragraph { codeBlock { rule.compliantCodeExample } }
        }
    }
}

package io.gitlab.arturbosch.detekt.generator.printer

import io.github.detekt.utils.MarkdownContent
import io.github.detekt.utils.bold
import io.github.detekt.utils.codeBlock
import io.github.detekt.utils.crossOut
import io.github.detekt.utils.h3
import io.github.detekt.utils.h4
import io.github.detekt.utils.markdown
import io.github.detekt.utils.paragraph
import io.gitlab.arturbosch.detekt.generator.collection.Active
import io.gitlab.arturbosch.detekt.generator.collection.Rule

internal object RulePrinter : DocumentationPrinter<Rule> {

    override fun print(item: Rule): String {
        return markdown {
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

            if (item.requiresTypeResolution) {
                paragraph {
                    bold { "Requires Type Resolution" }
                }
            }

            if (item.debt.isNotEmpty()) {
                paragraph {
                    "${bold { "Debt" }}: ${item.debt}"
                }
            }

            if (!item.aliases.isNullOrEmpty()) {
                paragraph {
                    "${bold { "Aliases" }}: ${item.aliases}"
                }
            }

            markdown { RuleConfigurationPrinter.print(item.configurations) }

            printRuleCodeExamples(item)
        }
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

package io.gitlab.arturbosch.detekt.generator.printer

import io.gitlab.arturbosch.detekt.generator.collection.Active
import io.gitlab.arturbosch.detekt.generator.collection.Rule
import io.gitlab.arturbosch.detekt.generator.out.MarkdownContent
import io.gitlab.arturbosch.detekt.generator.out.bold
import io.gitlab.arturbosch.detekt.generator.out.codeBlock
import io.gitlab.arturbosch.detekt.generator.out.h3
import io.gitlab.arturbosch.detekt.generator.out.h4
import io.gitlab.arturbosch.detekt.generator.out.markdown
import io.gitlab.arturbosch.detekt.generator.out.paragraph

internal object RulePrinter : DocumentationPrinter<Rule> {

    override fun print(item: Rule): String {
        return markdown {
            h3 { item.name }

            if (item.description.isNotEmpty()) {
                paragraph { escapeHtml(item.description) }
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

            markdown { RuleConfigurationPrinter.print(item.configuration) }

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

    internal fun escapeHtml(input: String) = input
        .replace("<", "&lt;")
        .replace(">", "&gt;")
}

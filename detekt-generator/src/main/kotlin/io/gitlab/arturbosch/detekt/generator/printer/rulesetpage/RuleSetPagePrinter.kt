package io.gitlab.arturbosch.detekt.generator.printer.rulesetpage

import io.gitlab.arturbosch.detekt.generator.collection.Active
import io.gitlab.arturbosch.detekt.generator.collection.Rule
import io.gitlab.arturbosch.detekt.generator.out.MarkdownContent
import io.gitlab.arturbosch.detekt.generator.out.bold
import io.gitlab.arturbosch.detekt.generator.out.code
import io.gitlab.arturbosch.detekt.generator.out.codeBlock
import io.gitlab.arturbosch.detekt.generator.out.crossOut
import io.gitlab.arturbosch.detekt.generator.out.description
import io.gitlab.arturbosch.detekt.generator.out.h3
import io.gitlab.arturbosch.detekt.generator.out.h4
import io.gitlab.arturbosch.detekt.generator.out.item
import io.gitlab.arturbosch.detekt.generator.out.list
import io.gitlab.arturbosch.detekt.generator.out.markdown
import io.gitlab.arturbosch.detekt.generator.out.paragraph
import io.gitlab.arturbosch.detekt.generator.printer.DocumentationPrinter

object RuleSetPagePrinter : DocumentationPrinter<RuleSetPage> {

    override fun print(item: RuleSetPage): String {
        return markdown {
            if (item.ruleSet.description.isNotEmpty()) {
                paragraph { item.ruleSet.description }
            } else {
                paragraph { "TODO: Specify description" }
            }
            item.rules.forEach {
                markdown { printRule(it) }
            }
        }
    }

    private fun printRule(rule: Rule): String {
        return markdown {
            h3 { rule.name }

            if (rule.description.isNotEmpty()) {
                paragraph { rule.description }
            } else {
                paragraph { "TODO: Specify description" }
            }

            paragraph {
                "${bold { "Active by default" }}: ${if (rule.defaultActivationStatus.active) "Yes" else "No"}" +
                    ((rule.defaultActivationStatus as? Active)?.let { " - Since v${it.since}" } ?: "")
            }

            if (rule.requiresTypeResolution) {
                paragraph {
                    bold { "Requires Type Resolution" }
                }
            }

            if (rule.debt.isNotEmpty()) {
                paragraph {
                    "${bold { "Debt" }}: ${rule.debt}"
                }
            }

            if (!rule.aliases.isNullOrEmpty()) {
                paragraph {
                    "${bold { "Aliases" }}: ${rule.aliases}"
                }
            }

            if (rule.configuration.isNotEmpty()) {
                h4 { "Configuration options:" }
                list {
                    rule.configuration.forEach {
                        val defaultValues = formatDefaultValues(it.defaultValue)
                        val defaultAndroidValues = it.defaultAndroidValue?.let(::formatDefaultValues)
                        val defaultString = if (defaultAndroidValues != null) {
                            "(default: ${code { defaultValues }}) (android default: ${code { defaultAndroidValues }})"
                        } else {
                            "(default: ${code { defaultValues }})"
                        }
                        if (it.isDeprecated()) {
                            item {
                                crossOut { code { it.name } } + " " + defaultString
                            }
                            description { "${bold { "Deprecated" }}: ${it.deprecated}" }
                        } else {
                            item {
                                code { it.name } + " " + defaultString
                            }
                        }
                        description { it.description }
                    }
                }
            }

            printRuleCodeExamples(rule)
        }
    }

    private fun formatDefaultValues(rawString: String) = rawString.lines().joinToString {
        it.trim().removePrefix("- ")
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

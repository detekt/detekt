package io.gitlab.arturbosch.detekt.generator.printer.rulesetpage

import io.gitlab.arturbosch.detekt.generator.collection.Rule
import io.gitlab.arturbosch.detekt.generator.out.code
import io.gitlab.arturbosch.detekt.generator.out.description
import io.gitlab.arturbosch.detekt.generator.out.h1
import io.gitlab.arturbosch.detekt.generator.out.h2
import io.gitlab.arturbosch.detekt.generator.out.h3
import io.gitlab.arturbosch.detekt.generator.out.h4
import io.gitlab.arturbosch.detekt.generator.out.item
import io.gitlab.arturbosch.detekt.generator.out.kotlinCode
import io.gitlab.arturbosch.detekt.generator.out.list
import io.gitlab.arturbosch.detekt.generator.out.markdown
import io.gitlab.arturbosch.detekt.generator.out.orderedList
import io.gitlab.arturbosch.detekt.generator.out.paragraph
import io.gitlab.arturbosch.detekt.generator.out.referenceToHeading
import io.gitlab.arturbosch.detekt.generator.printer.DocumentationPrinter

/**
 * @author Marvin Ramin
 */
object RuleSetPagePrinter : DocumentationPrinter<RuleSetPage> {

	override fun print(item: RuleSetPage): String {
		return markdown {
			h1 { item.ruleSet.name }
			if (item.ruleSet.description.isNotEmpty()) {
				paragraph { item.ruleSet.description }
			} else {
				paragraph { "TODO: Specify description" }
			}

			if (item.rules.isNotEmpty()) {
				h2 { "Content" }
				val sections = item.rules.map { referenceToHeading { it.name } }
				orderedList { sections }
				h2 { "Rules in the ${code { item.ruleSet.name } } rule set:" }
				item.rules.forEach {
					markdown { printRule(it) }
				}
			} else {
				paragraph { "This RuleSet does not have any rules associated with it at the moment." }
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

			if (rule.configuration.isNotEmpty()) {
				h4 { "Configuration options:" }
				list {
					rule.configuration.forEach {
						item { "${code { it.name} } (default: ${code { it.defaultValue } })" }
						description { it.description }
					}
				}
			}

			if (rule.nonCompliantCodeExample.isNotEmpty()) {
				h4 { "Noncompliant Code:" }
				paragraph { kotlinCode { rule.nonCompliantCodeExample } }
			}

			if (rule.compliantCodeExample.isNotEmpty()) {
				h4 { "Compliant Code:" }
				paragraph { kotlinCode { rule.compliantCodeExample } }
			}
		}
	}
}

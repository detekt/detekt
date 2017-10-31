package io.gitlab.arturbosch.detekt.generator.printer

import io.gitlab.arturbosch.detekt.generator.collection.Rule
import io.gitlab.arturbosch.detekt.generator.collection.RuleSetProvider

/**
 * @author Marvin Ramin
 */
data class RuleSetPage(
		val ruleSet: RuleSetProvider,
		val rules: List<Rule>
)

object RuleSetPagePrinter : DocumentationPrinter<RuleSetPage> {

	override fun print(item: RuleSetPage): String {
		var output = ""
		output += "# ${item.ruleSet.name}\n"
		output += "\n"
		if (item.ruleSet.description.isNotEmpty()) {
			output += "${item.ruleSet.description}\n"
			output += "\n"
		} else {
			output += "TODO: Specify description \n"
			output += "\n"
		}

		if (item.rules.isNotEmpty()) {
			output += "## Rules in the `${item.ruleSet.name}` rule set:\n"
			item.rules.forEach {
				output += printRule(it)
			}
		} else {
			output += "This RuleSet does not have any rules associated with it at the moment.\n"
		}

		return output
	}

	private fun printRule(rule: Rule): String {
		var ruleOutput = ""

		ruleOutput += "### ${rule.name}\n"
		ruleOutput += "\n"
		if (rule.description.isNotEmpty()) {
			ruleOutput += "${rule.description}\n"
			ruleOutput += "\n"
		} else {
			ruleOutput += "TODO: Specify description \n"
			ruleOutput += "\n"
		}

		if (rule.configuration.isNotEmpty()) {
			ruleOutput += "#### Configuration options: \n"
			rule.configuration.forEach {
				ruleOutput += "* ${it.name} (default: ${it.defaultValue})\n\n"
				ruleOutput += "   ${it.description}\n\n"
			}
		}

		return ruleOutput
	}
}

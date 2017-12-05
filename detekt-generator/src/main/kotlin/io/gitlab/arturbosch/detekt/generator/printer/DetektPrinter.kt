package io.gitlab.arturbosch.detekt.generator.printer

import io.gitlab.arturbosch.detekt.generator.Args
import io.gitlab.arturbosch.detekt.generator.out.markdownFile
import io.gitlab.arturbosch.detekt.generator.out.yamlFile
import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.ConfigPrinter
import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.RuleSetPage
import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.RuleSetPagePrinter

class DetektPrinter(private val arguments: Args) {

	fun print(pages: List<RuleSetPage>) {
		pages.forEach {
			markdownFile(arguments.documentationPath, it.ruleSet.name) { RuleSetPagePrinter.print(it) }
		}

		yamlFile(arguments.configPath, "default-detekt-config") { ConfigPrinter.print(pages) }
	}

}

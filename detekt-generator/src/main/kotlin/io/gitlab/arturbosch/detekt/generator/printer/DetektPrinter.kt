package io.gitlab.arturbosch.detekt.generator.printer

import io.gitlab.arturbosch.detekt.generator.out.markdownFile
import io.gitlab.arturbosch.detekt.generator.out.yamlFile
import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.ConfigPrinter
import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.RuleSetPage
import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.RuleSetPagePrinter

class DetektPrinter {

	fun print(pages: List<RuleSetPage>) {
		pages.forEach {
			markdownFile(it.ruleSet.name) { RuleSetPagePrinter.print(it) }
		}

		yamlFile("default-detekt-config") { ConfigPrinter.print(pages) }
	}

}

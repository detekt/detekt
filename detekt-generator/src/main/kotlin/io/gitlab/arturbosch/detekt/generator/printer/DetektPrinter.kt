package io.gitlab.arturbosch.detekt.generator.printer

import io.gitlab.arturbosch.detekt.generator.out.markdownFile
import io.gitlab.arturbosch.detekt.generator.out.yamlFile
import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.ConfigPrinter
import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.RuleSetPage
import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.RuleSetPagePrinter
import java.nio.file.Path

class DetektPrinter {

	fun print(path: Path, pages: List<RuleSetPage>) {
		pages.forEach {
			markdownFile(path, it.ruleSet.name) { RuleSetPagePrinter.print(it) }
		}

		yamlFile(path, "default-detekt-config") { ConfigPrinter.print(pages) }
	}

}

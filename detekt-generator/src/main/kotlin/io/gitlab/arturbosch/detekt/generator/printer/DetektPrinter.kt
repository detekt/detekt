package io.gitlab.arturbosch.detekt.generator.printer

import io.gitlab.arturbosch.detekt.generator.Args
import io.gitlab.arturbosch.detekt.generator.out.MarkdownWriter
import io.gitlab.arturbosch.detekt.generator.out.YamlWriter
import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.ConfigPrinter
import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.RuleSetPage
import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.RuleSetPagePrinter

class DetektPrinter(private val arguments: Args) {

	private val markdownWriter = MarkdownWriter()
	private val yamlWriter = YamlWriter()

	fun print(pages: List<RuleSetPage>) {
		pages.forEach {
			markdownWriter.write(arguments.documentationPath, it.ruleSet.name) { RuleSetPagePrinter.print(it) }
		}
		yamlWriter.write(arguments.configPath, "default-detekt-config") { ConfigPrinter.print(pages) }
	}

}

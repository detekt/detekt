package io.gitlab.arturbosch.detekt.generator.printer

import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.RuleSetPagePrinter
import io.gitlab.arturbosch.detekt.generator.util.createRuleSetPage
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import java.io.File

class RuleSetPagePrinterSpec : Spek({

	given("a config to print") {

		it("prints the correct markdown format") {
			val markdownString = RuleSetPagePrinter.print(createRuleSetPage())
			val expectedMarkdownString = File(resource("/RuleSet.md")).readText()
			assertThat(markdownString).isEqualTo(expectedMarkdownString)
		}
	}
})

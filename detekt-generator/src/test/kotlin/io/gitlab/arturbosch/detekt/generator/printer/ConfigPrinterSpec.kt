package io.gitlab.arturbosch.detekt.generator.printer

import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.ConfigPrinter
import io.gitlab.arturbosch.detekt.generator.util.createRuleSetPage
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import java.io.File

class ConfigPrinterSpec : Spek({

	given("a config to print") {

		it("prints the correct yaml format") {
			val ruleSetList = listOf(createRuleSetPage())
			val yamlString = ConfigPrinter.print(ruleSetList)
			val expectedYamlString = File(resource("/RuleSetConfig.yml")).readText()
			assertThat(yamlString).contains(expectedYamlString)
		}
	}
})

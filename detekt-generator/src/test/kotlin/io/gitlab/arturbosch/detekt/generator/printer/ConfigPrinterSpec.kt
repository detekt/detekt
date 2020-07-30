package io.gitlab.arturbosch.detekt.generator.printer

import io.github.detekt.test.utils.readResourceContent
import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.ConfigPrinter
import io.gitlab.arturbosch.detekt.generator.util.createRuleSetPage
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ConfigPrinterSpec : Spek({

    describe("Config printer") {

        it("prints the correct yaml format") {
            val ruleSetList = listOf(createRuleSetPage())
            val expectedRulePart = readResourceContent("RuleSetConfig.yml")

            val yamlString = ConfigPrinter.print(ruleSetList)

            assertThat(yamlString).contains(expectedRulePart)
            assertThat(yamlString).contains("output-reports:")
            assertThat(yamlString).contains("console-reports:")
            assertThat(yamlString).contains("processors:")
        }
    }
})

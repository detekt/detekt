package io.gitlab.arturbosch.detekt.generator.printer

import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.ConfigPrinter
import io.gitlab.arturbosch.detekt.generator.util.createRuleSetPage
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File

class ConfigPrinterSpec : Spek({

    describe("Config printer") {

        it("prints the correct yaml format") {
            val ruleSetList = listOf(createRuleSetPage())
            val yamlString = ConfigPrinter.print(ruleSetList)
            val expectedYamlString = File(resource("/RuleSetConfig.yml")).readText()
            assertThat(yamlString).contains(expectedYamlString)
        }
    }
})

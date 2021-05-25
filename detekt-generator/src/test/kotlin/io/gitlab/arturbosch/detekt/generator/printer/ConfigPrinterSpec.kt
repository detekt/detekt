package io.gitlab.arturbosch.detekt.generator.printer

import io.github.detekt.test.utils.readResourceContent
import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.ConfigPrinter
import io.gitlab.arturbosch.detekt.generator.util.createRuleSetPage
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ConfigPrinterSpec : Spek({

    describe("Config printer") {
        val ruleSetPage by memoized { createRuleSetPage() }
        val yamlString by memoized { ConfigPrinter.print(listOf(ruleSetPage)) }

        it("prints the rule set in the correct yaml format") {
            val expectedRulePart = readResourceContent("RuleSetConfig.yml")

            assertThat(yamlString).contains(expectedRulePart)
        }
        it("prints default build configuration") {
            assertThat(yamlString).contains("build:")
        }
        it("prints default config configuration") {
            assertThat(yamlString).contains("config:")
        }
        it("prints default processor configuration") {
            assertThat(yamlString).contains("processors:")
        }
        it("prints default report configuration") {
            assertThat(yamlString).contains("output-reports:")
            assertThat(yamlString).contains("console-reports:")
        }
        it("omits deprecated ruleset properties") {
            assertThat(yamlString).doesNotContain("deprecatedSimpleConfig")
            assertThat(yamlString).doesNotContain("deprecatedListConfig")
        }
        it("omits deprecated rule properties") {
            assertThat(yamlString).doesNotContain("conf2")
            assertThat(yamlString).doesNotContain("conf4")
        }
    }
})

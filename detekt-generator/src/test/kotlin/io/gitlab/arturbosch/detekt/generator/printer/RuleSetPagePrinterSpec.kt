package io.gitlab.arturbosch.detekt.generator.printer

import io.github.detekt.test.utils.resource
import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.RuleSetPagePrinter
import io.gitlab.arturbosch.detekt.generator.util.createRuleSetPage
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File

class RuleSetPagePrinterSpec : Spek({

    describe("Ruleset page printer") {
        it("prints the correct markdown format") {
            val markdownString = RuleSetPagePrinter.print(createRuleSetPage())
            val expectedMarkdownString = File(resource("/RuleSet.md")).readText()
            assertThat(markdownString).isEqualTo(expectedMarkdownString)
        }
    }
})

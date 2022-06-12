package io.gitlab.arturbosch.detekt.generator.printer

import io.github.detekt.test.utils.resource
import io.gitlab.arturbosch.detekt.generator.util.createRuleSetPage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

class RuleSetPagePrinterSpec {

    @Test
    fun `prints the correct markdown format`() {
        val markdownString = RuleSetPagePrinter.print(createRuleSetPage())
        val expectedMarkdownString = File(resource("/RuleSet.md")).readText()
        assertThat(markdownString).isEqualTo(expectedMarkdownString)
    }
}

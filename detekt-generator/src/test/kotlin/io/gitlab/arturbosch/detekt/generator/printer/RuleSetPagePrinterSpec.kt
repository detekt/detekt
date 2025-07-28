package io.gitlab.arturbosch.detekt.generator.printer

import dev.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.generator.util.createRuleSetPage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.io.path.readText

class RuleSetPagePrinterSpec {

    @Test
    fun `prints the correct markdown format`() {
        val markdownString = RuleSetPagePrinter.print(createRuleSetPage())
        val expectedMarkdownString = resourceAsPath("/RuleSet.md").readText()
        assertThat(markdownString).isEqualTo(expectedMarkdownString)
    }
}

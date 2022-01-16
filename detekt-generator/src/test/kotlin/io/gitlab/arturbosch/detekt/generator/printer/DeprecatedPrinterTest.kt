package io.gitlab.arturbosch.detekt.generator.printer

import io.gitlab.arturbosch.detekt.generator.util.createRuleSetPage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DeprecatedPrinterTest {

    @Nested
    inner class `Deprecated page printer` {
        @Test
        fun `prints the correct properties`() {
            val markdownString = DeprecatedPrinter.print(listOf(createRuleSetPage()))
            val expectedMarkdownString = """
                style>WildcardImport>conf2=use conf1 instead
                style>WildcardImport>conf4=use conf3 instead
                
            """.trimIndent()
            assertThat(markdownString).isEqualTo(expectedMarkdownString)
        }
    }
}

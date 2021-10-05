package io.gitlab.arturbosch.detekt.generator.printer

import io.gitlab.arturbosch.detekt.generator.util.createRuleSetPage
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class DeprecatedPrinterSpec : Spek({

    describe("Deprecated page printer") {
        it("prints the correct properties") {
            val markdownString = DeprecatedPrinter.print(listOf(createRuleSetPage()))
            val expectedMarkdownString = """
                style>WildcardImport>conf2=use conf1 instead
                style>WildcardImport>conf4=use conf3 instead
                
            """.trimIndent()
            assertThat(markdownString).isEqualTo(expectedMarkdownString)
        }
    }
})

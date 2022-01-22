package io.gitlab.arturbosch.detekt.generator.printer

import io.github.detekt.test.utils.createTempFileForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CliOptionsPrinterSpec {

    @Nested
    inner class `Cli Options Printer` {

        @Test
        fun `prints the correct cli-options_md`() {
            val cliOptionsFile = createTempFileForTest("cli-options", ".md")
            CliOptionsPrinter().print(cliOptionsFile.toAbsolutePath())
            val markdownString = cliOptionsFile.toFile().readText()

            assertThat(markdownString).contains("Usage: detekt [options]")
            assertThat(markdownString).contains("--input, -i")
        }
    }
}

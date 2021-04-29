package io.gitlab.arturbosch.detekt.generator.printer

import io.github.detekt.test.utils.createTempFileForTest
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class CliOptionsPrinterSpec : Spek({

    describe("Cli Options Printer") {

        it("prints the correct cli-options.md") {
            val cliOptionsFile = createTempFileForTest("cli-options", ".md")
            CliOptionsPrinter().print(cliOptionsFile.toAbsolutePath())
            val markdownString = cliOptionsFile.toFile().readText()

            assertThat(markdownString).contains("Usage: detekt [options]")
            assertThat(markdownString).contains("--input, -i")
        }
    }
})

package io.gitlab.arturbosch.detekt.generator.printer

import io.github.detekt.test.utils.createTempDirectoryForTest
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File

class CliOptionsPrinterSpec : Spek({

    describe("Cli Options Printer") {

        it("prints the correct cli-options.md") {
            val cliDir = createTempDirectoryForTest("cli")
            CliOptionsPrinter().print(cliDir.toAbsolutePath())
            val markdownString = File(cliDir.toFile(), "cli-options.md").readText()

            assertThat(markdownString).contains("title: Command Line Interface Options")
            assertThat(markdownString).contains("Usage: detekt [options]")
            assertThat(markdownString).contains("--input, -i")
        }
    }
})

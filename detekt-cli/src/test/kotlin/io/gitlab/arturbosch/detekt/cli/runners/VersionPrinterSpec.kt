package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.test.utils.StringPrintStream
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class VersionPrinterSpec : Spek({

    describe("version printer") {

        it("prints the version") {
            val printStream = StringPrintStream()

            VersionPrinter(printStream).execute()

            assertThat(printStream.toString()).isEqualTo("1.6.0" + System.lineSeparator())
        }
    }
})

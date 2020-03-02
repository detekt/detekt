package io.gitlab.arturbosch.detekt.cli.runners

import org.assertj.core.api.Assertions
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class VersionPrinterSpec : Spek({

    describe("version printer") {

        val outStream = System.out
        val output = ByteArrayOutputStream()

        beforeEachTest {
            System.setOut(PrintStream(output))
        }

        afterEachTest {
            System.setOut(outStream)
        }

        it("should print the detekt version") {
            val printer = VersionPrinter()
            printer.execute()
            Assertions.assertThat(output.toString()).isNotEmpty()
        }
    }
})

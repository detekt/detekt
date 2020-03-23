package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.test.StringPrintStream
import io.gitlab.arturbosch.detekt.test.TestDetektion
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ConsoleReportSpec : Spek({

    describe("print rendered reports") {
        it("render a string") {
            val output = printReport("hello")
            assertThat(output).startsWith("hello")
        }
    }

    describe("print empty reports") {
        it("does not print when text = null") {
            val output = printReport(null)
            assertThat(output).isEmpty()
        }

        it("does not print when text is empty") {
            val output = printReport("")
            assertThat(output).isEmpty()
        }

        it("does not print when text is blank") {
            val output = printReport(" \n\t ")
            assertThat(output).isEmpty()
        }
    }
})

private fun printReport(str: String?): String {
    val detektion = TestDetektion()
    val printerStream = StringPrintStream()
    val report = object : ConsoleReport() {
        override fun render(detektion: Detektion): String? = str
    }

    report.print(printerStream, detektion)
    return printerStream.toString()
}

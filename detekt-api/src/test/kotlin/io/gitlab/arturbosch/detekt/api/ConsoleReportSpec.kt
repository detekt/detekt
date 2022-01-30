package io.gitlab.arturbosch.detekt.api

import io.github.detekt.test.utils.StringPrintStream
import io.gitlab.arturbosch.detekt.test.TestDetektion
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ConsoleReportSpec {

    @Nested
    inner class `print rendered reports` {

        @Test
        fun `render a string`() {
            val output = printReport("hello")
            assertThat(output).startsWith("hello")
        }
    }

    @Nested
    inner class `print empty reports` {

        @Test
        fun `does not print when text = null`() {
            val output = printReport(null)
            assertThat(output).isEmpty()
        }

        @Test
        fun `does not print when text is empty`() {
            val output = printReport("")
            assertThat(output).isEmpty()
        }

        @Test
        fun `does not print when text is blank`() {
            val output = printReport(" \n\t ")
            assertThat(output).isEmpty()
        }
    }
}

private fun printReport(str: String?): String {
    val detektion = TestDetektion()
    val printerStream = StringPrintStream()
    val report = object : ConsoleReport() {
        override fun render(detektion: Detektion): String? = str
    }
    @Suppress("DEPRECATION")
    report.print(printerStream, detektion)
    return printerStream.toString()
}

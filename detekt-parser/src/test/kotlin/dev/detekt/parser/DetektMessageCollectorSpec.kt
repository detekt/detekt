package dev.detekt.parser

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DetektMessageCollectorSpec {

    private val printer = FakePrinter()
    private lateinit var subject: DetektMessageCollector

    @BeforeEach
    fun setupFakesAndSubject() {
        printer.messages.clear()
        subject = DetektMessageCollector(
            minSeverity = CompilerMessageSeverity.INFO,
            printer = printer,
        )
    }

    @Nested
    inner class `message with min severity` {
        @BeforeEach
        fun setUp() {
            subject.report(CompilerMessageSeverity.INFO, "message", null)
        }

        @Test
        fun `prints the message`() {
            assertThat(printer.messages).contains("info: message")
        }

        @Test
        fun `adds up to the message count`() {
            subject.printIssuesCountIfAny()

            assertThat(printer.messages).contains(
                "There were 1 compiler errors found during analysis. This affects accuracy of reporting."
            )
        }
    }

    @Nested
    inner class `message with higher severity than the min severity` {
        @BeforeEach
        fun setUp() {
            subject.report(CompilerMessageSeverity.WARNING, "message", null)
        }

        @Test
        fun `prints the message`() {
            assertThat(printer.messages).contains("warning: message")
        }

        @Test
        fun `adds up to the message count`() {
            subject.printIssuesCountIfAny()

            assertThat(printer.messages).contains(
                "There were 1 compiler errors found during analysis. This affects accuracy of reporting."
            )
        }
    }

    @Nested
    inner class `message with lower severity than the min severity` {
        @BeforeEach
        fun setUp() {
            subject.report(CompilerMessageSeverity.LOGGING, "message", null)
        }

        @Test
        fun `ignores the message`() {
            assertThat(printer.messages).isEmpty()
        }
    }

    @Test
    fun `doesn't add up to the message count`() {
        subject.printIssuesCountIfAny()

        assertThat(printer.messages).isEmpty()
    }

    class FakePrinter : (String) -> Unit {
        val messages = mutableListOf<String>()
        override fun invoke(param: String) {
            messages.add(param)
        }
    }
}

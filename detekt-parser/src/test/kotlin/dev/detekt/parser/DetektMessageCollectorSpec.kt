package dev.detekt.parser

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DetektMessageCollectorSpec {

    private val debugPrinter = FakePrinter()
    private val warningPrinter = FakeWarningPrinter()
    private lateinit var subject: DetektMessageCollector

    @BeforeEach
    fun setupFakesAndSubject() {
        debugPrinter.messages.clear()
        warningPrinter.messages.clear()
        subject = DetektMessageCollector(
            minSeverity = CompilerMessageSeverity.INFO,
            debugPrinter = debugPrinter,
            warningPrinter = warningPrinter,
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
            assertThat(debugPrinter.messages).contains("info: message")
        }

        @Test
        fun `adds up to the message count`() {
            subject.printIssuesCountIfAny(k2Mode = true)

            assertThat(warningPrinter.messages).contains(
                """
                    There were 1 compiler errors found during analysis. This affects accuracy of reporting.
                    Run detekt CLI with --debug or set `detekt { debug = true }` in Gradle to see the error messages.
                """.trimIndent()
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
            assertThat(debugPrinter.messages).contains("warning: message")
        }

        @Test
        fun `adds up to the message count`() {
            subject.printIssuesCountIfAny(k2Mode = true)

            assertThat(warningPrinter.messages).contains(
                """
                    There were 1 compiler errors found during analysis. This affects accuracy of reporting.
                    Run detekt CLI with --debug or set `detekt { debug = true }` in Gradle to see the error messages.
                """.trimIndent()
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
            assertThat(debugPrinter.messages).isEmpty()
        }
    }

    @Test
    fun `doesn't add up to the message count`() {
        subject.printIssuesCountIfAny()

        assertThat(warningPrinter.messages).isEmpty()
    }

    @Nested
    inner class `debug enabled omits suggestion` {
        @BeforeEach
        fun setUp() {
            debugPrinter.messages.clear()
            warningPrinter.messages.clear()
            subject = DetektMessageCollector(
                minSeverity = CompilerMessageSeverity.INFO,
                debugPrinter = debugPrinter,
                warningPrinter = warningPrinter,
                isDebugEnabled = true,
            )
            subject.report(CompilerMessageSeverity.ERROR, "boom", null)
        }

        @Test
        fun `prints header without suggestion`() {
            subject.printIssuesCountIfAny(k2Mode = false)
            assertThat(warningPrinter.messages).containsExactly(
                "There were 1 compiler errors found during legacy compiler analysis. This affects accuracy of reporting."
            )
        }
    }

    class FakePrinter : (() -> String) -> Unit {
        val messages = mutableListOf<String>()
        override fun invoke(param: () -> String) {
            messages.add(param())
        }
    }

    class FakeWarningPrinter : (String) -> Unit {
        val messages = mutableListOf<String>()
        override fun invoke(param: String) {
            messages.add(param)
        }
    }
}

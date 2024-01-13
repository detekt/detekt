package io.gitlab.arturbosch.detekt.core

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DetektMessageCollectorSpec {

    private val debugPrinter = FakePrinter()
    private lateinit var subject: DetektMessageCollector

    @BeforeEach
    fun setupFakesAndSubject() {
        debugPrinter.messages.clear()
        subject = DetektMessageCollector(
            minSeverity = CompilerMessageSeverity.INFO,
            debugPrinter = debugPrinter,
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

   class FakePrinter : (() -> String) -> Unit {
        val messages = mutableListOf<String>()
        override fun invoke(param: () -> String) {
            messages.add(param())
        }
    }
}

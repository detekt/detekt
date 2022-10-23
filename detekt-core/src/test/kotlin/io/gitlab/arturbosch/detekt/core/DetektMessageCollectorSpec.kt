package io.gitlab.arturbosch.detekt.core

import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DetektMessageCollectorSpec {

    private lateinit var debugPrinter: (() -> String) -> Unit
    private lateinit var subject: DetektMessageCollector

    @BeforeEach
    fun setupMocksAndSubject() {
        debugPrinter = mockk { every { this@mockk.invoke(any()) } returns Unit }
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
            val slot = slot<() -> String>()
            verify { debugPrinter.invoke(capture(slot)) }
            assertThat(slot.captured()).isEqualTo("info: message")
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
            val slot = slot<() -> String>()
            verify { debugPrinter.invoke(capture(slot)) }
            assertThat(slot.captured()).isEqualTo("warning: message")
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
            verify { debugPrinter wasNot Called }
        }
    }
}

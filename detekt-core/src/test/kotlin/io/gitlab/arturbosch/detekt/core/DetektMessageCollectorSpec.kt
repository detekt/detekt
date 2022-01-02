package io.gitlab.arturbosch.detekt.core

import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.spekframework.spek2.Spek
import org.spekframework.spek2.lifecycle.CachingMode
import org.spekframework.spek2.style.specification.describe

internal object DetektMessageCollectorSpec : Spek({
    describe("DetektMessageCollector") {
        val debugPrinter: (() -> String) -> Unit by memoized(CachingMode.TEST) {
            mockk {
                every { this@mockk.invoke(any()) } returns Unit
            }
        }
        val warningPrinter: (String) -> Unit by memoized(CachingMode.TEST) {
            mockk {
                every { this@mockk.invoke(any()) } returns Unit
            }
        }
        val subject by memoized(CachingMode.TEST) {
            DetektMessageCollector(
                minSeverity = CompilerMessageSeverity.INFO,
                debugPrinter = debugPrinter,
                warningPrinter = warningPrinter,
            )
        }

        describe("message with min severity") {
            beforeEachTest { subject.report(CompilerMessageSeverity.INFO, "message", null) }

            it("prints the message") {
                val slot = slot<() -> String>()
                verify { debugPrinter.invoke(capture(slot)) }
                assertThat(slot.captured()).isEqualTo("info: message")
            }

            it("adds up to the message count") {
                subject.printIssuesCountIfAny()

                verify {
                    warningPrinter(
                        "The BindingContext was created with 1 issues. " +
                            "Run detekt with --debug to see the error messages."
                    )
                }
            }
        }
        describe("message with higher severity than the min severity") {
            beforeEachTest { subject.report(CompilerMessageSeverity.WARNING, "message", null) }

            it("prints the message") {
                val slot = slot<() -> String>()
                verify { debugPrinter.invoke(capture(slot)) }
                assertThat(slot.captured()).isEqualTo("warning: message")
            }

            it("adds up to the message count") {
                subject.printIssuesCountIfAny()

                verify {
                    warningPrinter(
                        "The BindingContext was created with 1 issues. " +
                            "Run detekt with --debug to see the error messages."
                    )
                }
            }
        }
        describe("message with lower severity than the min severity") {
            beforeEachTest { subject.report(CompilerMessageSeverity.LOGGING, "message", null) }

            it("ignores the message") {
                verify { debugPrinter wasNot Called }
            }

            it("doesn't add up to the message count") {
                subject.printIssuesCountIfAny()

                verify { warningPrinter wasNot Called }
            }
        }
    }
})

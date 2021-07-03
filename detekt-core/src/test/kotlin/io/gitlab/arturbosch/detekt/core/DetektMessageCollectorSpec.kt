package io.gitlab.arturbosch.detekt.core

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.PrintStream

internal object DetektMessageCollectorSpec : Spek({
    describe("DetektMessageCollector") {
        val errorStream by memoized { CollectingPrintStream() }
        val subject by memoized {
            DetektMessageCollector(
                errorStream = errorStream,
                minSeverity = CompilerMessageSeverity.INFO
            )
        }

        describe("message with min severity") {
            beforeEachTest { subject.report(CompilerMessageSeverity.INFO, "message", null) }
            it("prints the message") {
                assertThat(errorStream.messages).containsExactly("info: message")
            }
        }
        describe("message with higher severity than the min severity") {
            beforeEachTest { subject.report(CompilerMessageSeverity.WARNING, "message", null) }
            it("prints the message") {
                assertThat(errorStream.messages).containsExactly("warning: message")
            }
        }
        describe("message with lower severity than the min severity") {
            beforeEachTest { subject.report(CompilerMessageSeverity.LOGGING, "message", null) }
            it("ignores the message") {
                assertThat(errorStream.messages).isEmpty()
            }
        }
    }
})

private class CollectingPrintStream(val messages: MutableList<String> = mutableListOf()) : PrintStream(System.err) {
    override fun println(message: String) {
        messages.add(message)
    }
}

package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class PrintStackTraceSpec : SubjectSpek<PrintStackTrace>({
    subject { PrintStackTrace() }

    given("catch clauses with printStacktrace methods") {

        it("prints a stacktrace") {
            val code = """
				fun x() {
					try {
					} catch (e: Exception) {
						e.printStackTrace()
					}
				}"""
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("does not print a stacktrace") {
            val code = """
				fun x() {
					try {
					} catch (e: Exception) {
						e.foo()
						val bar = e.bar
						printStackTrace()
					}
				}"""
            assertThat(subject.lint(code)).hasSize(0)
        }
    }

    given("a stacktrace printed by a thread") {

        it("prints one") {
            val code = """
				fun x() {
					Thread.dumpStack()
					Foo.dumpStack()
				}"""
            assertThat(subject.lint(code)).hasSize(1)
        }
    }
})

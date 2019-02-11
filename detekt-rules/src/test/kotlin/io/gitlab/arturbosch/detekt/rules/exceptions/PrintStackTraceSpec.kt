package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class PrintStackTraceSpec : Spek({
    val subject by memoized { PrintStackTrace() }

    describe("PrintStackTrace") {

        context("catch clauses with printStacktrace methods") {

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

        context("a stacktrace printed by a thread") {

            it("prints one") {
                val code = """
				fun x() {
					Thread.dumpStack()
					UnusedPrivateMemberPositiveObject.Foo.dumpStack()
				}"""
                assertThat(subject.lint(code)).hasSize(1)
            }
        }
    }
})

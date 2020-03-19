package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.compileAndLint
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
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("does not print a stacktrace") {
                val code = """
                fun x() {
                    try {
                    } catch (e: Exception) {
                        e.fillInStackTrace()
                        val msg = e.message
                        fun printStackTrace() {}
                        printStackTrace()
                    }
                }
                """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }
        }

        context("a stacktrace printed by a thread") {

            it("prints one") {
                val code = """
                fun x() {
                    Thread.dumpStack()

                    fun dumpStack() {}
                    dumpStack()
                }"""
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }
        }
    }
})

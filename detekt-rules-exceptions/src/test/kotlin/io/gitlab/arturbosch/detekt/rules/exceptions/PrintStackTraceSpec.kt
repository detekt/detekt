package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PrintStackTraceSpec {
    val subject = PrintStackTrace()

    @Nested
    inner class `print stack trace rule` {

        @Nested
        inner class `catch clauses with printStacktrace methods` {

            @Test
            fun `prints a stacktrace`() {
                val code = """
                fun x() {
                    try {
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                """
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            @Test
            fun `does not print a stacktrace`() {
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

        @Nested
        inner class `a stacktrace printed by a thread` {

            @Test
            fun `prints one`() {
                val code = """
                fun x() {
                    Thread.dumpStack()

                    fun dumpStack() {}
                    dumpStack()
                }
                """
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }
        }
    }
}

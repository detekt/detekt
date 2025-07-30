package dev.detekt.rules.exceptions

import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PrintStackTraceSpec {
    val subject = PrintStackTrace(Config.empty)

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
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
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
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
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
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }
    }
}

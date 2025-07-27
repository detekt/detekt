package io.gitlab.arturbosch.detekt.rules.exceptions

import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ThrowingNewInstanceOfSameExceptionSpec {
    val subject = ThrowingNewInstanceOfSameException(Config.empty)

    @Nested
    inner class `a catch block which rethrows a new instance of the caught exception` {
        val code = """
            fun x() {
                try {
                } catch (e: IllegalStateException) {
                    throw IllegalStateException(e)
                }
            }
        """.trimIndent()

        @Test
        fun `should report`() {
            val findings = subject.lint(code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `a catch block which rethrows a new instance of another exception` {
        val code = """
            fun x() {
                try {
                } catch (e: IllegalStateException) {
                    throw IllegalArgumentException(e)
                }
            }
        """.trimIndent()

        @Test
        fun `should not report`() {
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    @DisplayName(
        "a catch block which throws a new instance of the same exception type without wrapping the caught exception"
    )
    inner class CatchBlockThrowingSameExceptionWithoutWrapping {
        val code = """
            fun x() {
                try {
                } catch (e: IllegalStateException) {
                    throw IllegalStateException()
                }
            }
        """.trimIndent()

        @Test
        fun `should not report`() {
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }
    }
}

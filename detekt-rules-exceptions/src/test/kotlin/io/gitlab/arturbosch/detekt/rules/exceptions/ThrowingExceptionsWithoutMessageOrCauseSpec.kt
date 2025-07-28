package io.gitlab.arturbosch.detekt.rules.exceptions

import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ThrowingExceptionsWithoutMessageOrCauseSpec {
    val subject = ThrowingExceptionsWithoutMessageOrCause(
        TestConfig("exceptions" to listOf("IllegalArgumentException"))
    )

    @Nested
    inner class `several exception calls` {

        val code = """
            fun x() {
                IllegalArgumentException(IllegalArgumentException())
                IllegalArgumentException("foo")
                throw IllegalArgumentException()
            }
        """.trimIndent()

        @Test
        fun `reports calls to the default constructor`() {
            assertThat(subject.lint(code)).hasSize(2)
        }

        @Test
        fun `does not report calls to the default constructor with empty configuration`() {
            val config = TestConfig("exceptions" to emptyList<String>())
            val findings = ThrowingExceptionsWithoutMessageOrCause(config).lint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Test
    fun `a test code which asserts an exception does not report a call to this exception`() {
        val code = """
            fun test() {
                org.assertj.core.api.Assertions.assertThatIllegalArgumentException().isThrownBy { println() }
            }
        """.trimIndent()
        assertThat(subject.lint(code, compile = false)).isEmpty()
    }

    @Test
    fun `don't raise an issue when only matches ignoring cases`() {
        val code = """
            fun illegalArgumentException() {
                // no-op
            }

            fun test() {
                illegalArgumentException()
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }
}

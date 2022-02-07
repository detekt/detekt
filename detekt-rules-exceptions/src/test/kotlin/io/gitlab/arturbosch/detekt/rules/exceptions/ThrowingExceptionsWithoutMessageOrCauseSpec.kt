package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ThrowingExceptionsWithoutMessageOrCauseSpec {
    val subject =
        ThrowingExceptionsWithoutMessageOrCause(
            TestConfig("exceptions" to listOf("IllegalArgumentException"))
        )

    @Nested
    inner class `ThrowingExceptionsWithoutMessageOrCause rule` {

        @Nested
        inner class `several exception calls` {

            val code = """
                fun x() {
                    IllegalArgumentException(IllegalArgumentException())
                    IllegalArgumentException("foo")
                    throw IllegalArgumentException()
                }"""

            @Test
            fun `reports calls to the default constructor`() {
                assertThat(subject.compileAndLint(code)).hasSize(2)
            }

            @Test
            fun `does not report calls to the default constructor with empty configuration`() {
                val config = TestConfig("exceptions" to emptyList<String>())
                val findings = ThrowingExceptionsWithoutMessageOrCause(config).compileAndLint(code)
                assertThat(findings).isEmpty()
            }
        }

        @Nested
        inner class `a test code which asserts an exception` {

            @Test
            fun `does not report a call to this exception`() {
                val code = """
                fun test() {
                    org.assertj.core.api.Assertions.assertThatIllegalArgumentException().isThrownBy { println() }
                }
            """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }
        }
    }
}

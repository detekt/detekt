package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

private const val EXCEPTION_NAMES = "exceptionNames"

class TooGenericExceptionThrownSpec {

    @ParameterizedTest
    @ValueSource(strings = ["Error", "Exception", "Throwable", "RuntimeException"])
    fun `should report $exceptionName`(exceptionName: String) {
        val rule = TooGenericExceptionThrown(TestConfig(EXCEPTION_NAMES to "[$exceptionName]"))

        assertThat(rule.compileAndLint(tooGenericExceptionCode)).hasSize(1)
    }

    @Test
    fun `should not report thrown exceptions`() {
        val rule = TooGenericExceptionThrown(TestConfig(EXCEPTION_NAMES to "['MyException', Bar]"))

        assertThat(rule.compileAndLint(tooGenericExceptionCode)).isEmpty()
    }

    @Test
    fun `should not report caught exceptions`() {
        val rule = TooGenericExceptionThrown(TestConfig(EXCEPTION_NAMES to "['Exception']"))
        val code = """
            fun f() {
                try {
                    throw Throwable()
                } catch (caught: Exception) {
                    throw Error()
                }
            }
        """.trimIndent()

        assertThat(rule.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should not report initialize exceptions`() {
        val rule = TooGenericExceptionThrown(TestConfig(EXCEPTION_NAMES to "['Exception']"))
        val code = """fun f() { val ex = Exception() }"""

        assertThat(rule.compileAndLint(code)).isEmpty()
    }
}

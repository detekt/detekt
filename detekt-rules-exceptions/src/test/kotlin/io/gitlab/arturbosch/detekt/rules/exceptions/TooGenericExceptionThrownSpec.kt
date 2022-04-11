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
        val config = TestConfig(mapOf(EXCEPTION_NAMES to "[$exceptionName]"))
        val rule = TooGenericExceptionThrown(config)

        val findings = rule.compileAndLint(tooGenericExceptionCode)

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should not report thrown exceptions`() {
        val config = TestConfig(mapOf(EXCEPTION_NAMES to "['MyException', Bar]"))
        val rule = TooGenericExceptionThrown(config)

        val findings = rule.compileAndLint(tooGenericExceptionCode)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `should not report caught exceptions`() {
        val config = TestConfig(mapOf(EXCEPTION_NAMES to "['Exception']"))
        val rule = TooGenericExceptionThrown(config)

        val code = """
            fun f() {
                try {
                    throw Throwable()
                } catch (caught: Exception) {
                    throw Error()
                }
            }
        """
        val findings = rule.compileAndLint(code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `should not report initialize exceptions`() {
        val config = TestConfig(mapOf(EXCEPTION_NAMES to "['Exception']"))
        val rule = TooGenericExceptionThrown(config)

        val code = """fun f() { val ex = Exception() }"""
        val findings = rule.compileAndLint(code)

        assertThat(findings).isEmpty()
    }
}

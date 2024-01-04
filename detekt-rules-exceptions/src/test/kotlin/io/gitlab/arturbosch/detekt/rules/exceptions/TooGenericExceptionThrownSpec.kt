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
        val code = """
            fun main() {
                try {
                    throw Throwable()
                } catch (e: ArrayIndexOutOfBoundsException) {
                    throw Error()
                } catch (e: Error) {
                    throw Exception()
                } catch (e: Exception) {
                } catch (e: IllegalMonitorStateException) {
                } catch (e: IndexOutOfBoundsException) {
                    throw RuntimeException()
                } catch (e: Throwable) {
                } catch (e: RuntimeException) {
                    throw NullPointerException()
                } catch (e: NullPointerException) {
                }
            }
        """.trimIndent()

        assertThat(rule.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `should not report thrown exceptions`() {
        val rule = TooGenericExceptionThrown(TestConfig(EXCEPTION_NAMES to "['MyException', Bar]"))
        val code = """
            fun main() {
                try {
                    throw Throwable()
                } catch (e: ArrayIndexOutOfBoundsException) {
                    throw Error()
                } catch (e: Error) {
                    throw Exception()
                } catch (e: Exception) {
                } catch (e: IllegalMonitorStateException) {
                } catch (e: IndexOutOfBoundsException) {
                    throw RuntimeException()
                } catch (e: Throwable) {
                } catch (e: RuntimeException) {
                    throw NullPointerException()
                } catch (e: NullPointerException) {
                }
            }
        """.trimIndent()

        assertThat(rule.compileAndLint(code)).isEmpty()
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

    @Test
    fun `should not report any`() {
        val rule = TooGenericExceptionThrown(TestConfig(EXCEPTION_NAMES to "[]"))
        val code = """
            fun main() {
                try {
                    throw Throwable()
                } catch (e: ArrayIndexOutOfBoundsException) {
                    throw Error()
                } catch (e: Error) {
                    throw Exception()
                } catch (e: Exception) {
                } catch (e: IllegalMonitorStateException) {
                } catch (e: IndexOutOfBoundsException) {
                    throw RuntimeException()
                } catch (e: Throwable) {
                } catch (e: RuntimeException) {
                    throw NullPointerException()
                } catch (e: NullPointerException) {

                }
            }
        """.trimIndent()
        val findings = rule.compileAndLint(code)

        assertThat(findings).isEmpty()
    }
}

package io.gitlab.arturbosch.detekt.rules.exceptions

import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.regex.PatternSyntaxException

private const val CAUGHT_EXCEPTIONS_PROPERTY = "exceptionNames"
private const val ALLOWED_EXCEPTION_NAME_REGEX = "allowedExceptionNameRegex"

class TooGenericExceptionCaughtSpec {
    @Test
    fun `a file with many caught exceptions should find one of each kind of defaults`() {
        val rule = TooGenericExceptionCaught(Config.empty)
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

        assertThat(rule.lint(code)).hasSameSizeAs(TooGenericExceptionCaught.caughtExceptionDefaults)
    }

    @Nested
    inner class `a file with a caught exception which is ignored` {

        val code = """
            fun f() {
                try {
                    throw Throwable()
                } catch (myIgnore: NullPointerException) {
                    throw Error()
                }
            }
        """.trimIndent()

        @Test
        fun `should not report an ignored catch blocks because of its exception name`() {
            val config = TestConfig(ALLOWED_EXCEPTION_NAME_REGEX to "myIgnore")
            val rule = TooGenericExceptionCaught(config)

            val findings = rule.lint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report an ignored catch blocks because of its exception type`() {
            val config = TestConfig(CAUGHT_EXCEPTIONS_PROPERTY to "[MyException]")
            val rule = TooGenericExceptionCaught(config)

            val findings = rule.lint(code)

            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class InvalidRegex {
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

        @Test
        fun `should fail with invalid regex on allowed exception names`() {
            val config = TestConfig(ALLOWED_EXCEPTION_NAME_REGEX to "*Foo")
            val rule = TooGenericExceptionCaught(config)
            assertThatExceptionOfType(PatternSyntaxException::class.java).isThrownBy {
                rule.lint(code)
            }
        }
    }

    @Test
    fun `should not report any`() {
        val rule = TooGenericExceptionCaught(TestConfig(CAUGHT_EXCEPTIONS_PROPERTY to "[]"))
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
        val findings = rule.lint(code)

        assertThat(findings).isEmpty()
    }
}

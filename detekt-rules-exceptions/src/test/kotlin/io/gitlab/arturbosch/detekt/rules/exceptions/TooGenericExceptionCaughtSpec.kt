package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.regex.PatternSyntaxException

private const val CAUGHT_EXCEPTIONS_PROPERTY = "exceptionNames"
private const val ALLOWED_EXCEPTION_NAME_REGEX = "allowedExceptionNameRegex"

class TooGenericExceptionCaughtSpec {

    @Nested
    inner class `a file with many caught exceptions` {

        @Test
        fun `should find one of each kind of defaults`() {
            val rule = TooGenericExceptionCaught(Config.empty)

            val findings = rule.compileAndLint(tooGenericExceptionCode)

            assertThat(findings).hasSize(TooGenericExceptionCaught.caughtExceptionDefaults.size)
        }
    }

    @Nested
    inner class `a file with a caught exception which is ignored` {

        val code = """
            class MyTooGenericException : RuntimeException()
            
            fun f() {
                try {
                    throw Throwable()
                } catch (myIgnore: MyTooGenericException) {
                    throw Error()
                }
            }
        """.trimIndent()

        @Test
        fun `should not report an ignored catch blocks because of its exception name`() {
            val config = TestConfig(ALLOWED_EXCEPTION_NAME_REGEX to "myIgnore")
            val rule = TooGenericExceptionCaught(config)

            val findings = rule.compileAndLint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report an ignored catch blocks because of its exception type`() {
            val config = TestConfig(CAUGHT_EXCEPTIONS_PROPERTY to "[MyException]")
            val rule = TooGenericExceptionCaught(config)

            val findings = rule.compileAndLint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not fail when disabled with invalid regex on allowed exception names`() {
            val config = TestConfig(
                "active" to "false",
                ALLOWED_EXCEPTION_NAME_REGEX to "*MyException",
            )
            val rule = TooGenericExceptionCaught(config)
            val findings = rule.compileAndLint(tooGenericExceptionCode)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should fail with invalid regex on allowed exception names`() {
            val config = TestConfig(ALLOWED_EXCEPTION_NAME_REGEX to "*Foo")
            val rule = TooGenericExceptionCaught(config)
            assertThatExceptionOfType(PatternSyntaxException::class.java).isThrownBy {
                rule.compileAndLint(tooGenericExceptionCode)
            }
        }
    }
}

package io.gitlab.arturbosch.detekt.rules.empty

import io.github.detekt.test.utils.compileForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.regex.PatternSyntaxException

private const val ALLOWED_EXCEPTION_NAME_REGEX = "allowedExceptionNameRegex"

class EmptyCodeSpec {

    val regexTestingCode = """
            fun f() {
                try {
                } catch (foo: Exception) {
                }
            }
    """

    @Nested
    inner class `EmptyCatchBlock rule` {

        @Test
        fun `findsEmptyCatch`() {
            test { EmptyCatchBlock(Config.empty) }
        }

        @Test
        fun `findsEmptyNestedCatch`() {
            val code = """
            fun f() {
                try {
                } catch (ignore: Exception) {
                    try {
                    } catch (e: Exception) {
                    }
                }
            }
            """
            assertThat(EmptyCatchBlock(Config.empty).compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `doesNotReportIgnoredOrExpectedException`() {
            val code = """
            fun f() {
                try {
                } catch (ignore: IllegalArgumentException) {
                } catch (expected: Exception) {
                }
            }
            """
            assertThat(EmptyCatchBlock(Config.empty).compileAndLint(code)).isEmpty()
        }

        @Test
        fun `doesNotReportEmptyCatchWithConfig`() {
            val code = """
            fun f() {
                try {
                } catch (foo: Exception) {
                }
            }
            """
            val config = TestConfig(mapOf(ALLOWED_EXCEPTION_NAME_REGEX to "foo"))
            assertThat(EmptyCatchBlock(config).compileAndLint(code)).isEmpty()
        }

        @Test
        fun `findsEmptyFinally`() {
            test { EmptyFinallyBlock(Config.empty) }
        }

        @Test
        fun `findsEmptyIf`() {
            test { EmptyIfBlock(Config.empty) }
        }

        @Test
        fun `findsEmptyElse`() {
            test { EmptyElseBlock(Config.empty) }
        }

        @Test
        fun `findsEmptyFor`() {
            test { EmptyForBlock(Config.empty) }
        }

        @Test
        fun `findsEmptyWhile`() {
            test { EmptyWhileBlock(Config.empty) }
        }

        @Test
        fun `findsEmptyDoWhile`() {
            test { EmptyDoWhileBlock(Config.empty) }
        }

        @Test
        fun `findsEmptyFun`() {
            test { EmptyFunctionBlock(Config.empty) }
        }

        @Test
        fun `findsEmptyClass`() {
            test { EmptyClassBlock(Config.empty) }
        }

        @Test
        fun `findsEmptyTry`() {
            test { EmptyTryBlock(Config.empty) }
        }

        @Test
        fun `findsEmptyWhen`() {
            test { EmptyWhenBlock(Config.empty) }
        }

        @Test
        fun `findsEmptyInit`() {
            test { EmptyInitBlock(Config.empty) }
        }

        @Test
        fun `findsOneEmptySecondaryConstructor`() {
            test { EmptySecondaryConstructor(Config.empty) }
        }

        @Test
        fun `doesNotFailWithInvalidRegexWhenDisabled`() {
            val configValues = mapOf(
                "active" to "false",
                ALLOWED_EXCEPTION_NAME_REGEX to "*foo"
            )
            val config = TestConfig(configValues)
            assertThat(EmptyCatchBlock(config).compileAndLint(regexTestingCode)).isEmpty()
        }

        @Test
        fun `doesFailWithInvalidRegex`() {
            val configValues = mapOf(ALLOWED_EXCEPTION_NAME_REGEX to "*foo")
            val config = TestConfig(configValues)
            assertThatExceptionOfType(PatternSyntaxException::class.java).isThrownBy {
                EmptyCatchBlock(config).compileAndLint(regexTestingCode)
            }
        }
    }
}

private fun test(block: () -> Rule) {
    val rule = block()
    rule.lint(compileForTest(resourceAsPath("Empty.kt")))
    assertThat(rule.findings).hasSize(1)
}

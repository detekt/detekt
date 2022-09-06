package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val IGNORE_OVERRIDABLE_FUNCTION = "ignoreOverridableFunction"
private const val IGNORE_ACTUAL_FUNCTION = "ignoreActualFunction"
private const val EXCLUDED_FUNCTIONS = "excludedFunctions"
private const val EXCLUDE_ANNOTATED_FUNCTION = "excludeAnnotatedFunction"

class FunctionOnlyReturningConstantSpec {
    val subject = FunctionOnlyReturningConstant()

    @Nested
    inner class `FunctionOnlyReturningConstant rule - positive cases` {

        val path = Case.FunctionReturningConstantPositive.path()

        val actualFunctionCode = """
            actual class ActualFunctionReturningConstant {
                actual fun f() = 1
            }
        """.trimIndent()

        val code = """
            import kotlin.SinceKotlin
            class Test {
                @SinceKotlin("1.0.0")
                fun someIgnoredFun(): String {
                    return "I am a constant"
                }
            }
        """.trimIndent()

        @Test
        fun `reports functions which return constants`() {
            assertThat(subject.lint(path)).hasSize(6)
        }

        @Test
        fun `reports overridden functions which return constants`() {
            val config = TestConfig(mapOf(IGNORE_OVERRIDABLE_FUNCTION to "false"))
            val rule = FunctionOnlyReturningConstant(config)
            assertThat(rule.lint(path)).hasSize(9)
        }

        @Test
        fun `does not report actual functions which return constants`() {
            assertThat(subject.lint(actualFunctionCode)).isEmpty()
        }

        @Test
        fun `reports actual functions which return constants`() {
            val config = TestConfig(mapOf(IGNORE_ACTUAL_FUNCTION to "false"))
            val rule = FunctionOnlyReturningConstant(config)
            assertThat(rule.lint(actualFunctionCode)).hasSize(1)
        }

        @Test
        fun `does not report excluded function which returns a constant (with string configuration)`() {
            val code = "fun f() = 1"
            val config = TestConfig(mapOf(EXCLUDED_FUNCTIONS to "f"))
            val rule = FunctionOnlyReturningConstant(config)
            assertThat(rule.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report excluded function which returns a constant`() {
            val code = "fun f() = 1"
            val config = TestConfig(mapOf(EXCLUDED_FUNCTIONS to listOf("f")))
            val rule = FunctionOnlyReturningConstant(config)
            assertThat(rule.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report wildcard excluded function which returns a constant`() {
            val code = "fun function() = 1"
            val config = TestConfig(mapOf(EXCLUDED_FUNCTIONS to listOf("f*ion")))
            val rule = FunctionOnlyReturningConstant(config)
            assertThat(rule.compileAndLint(code)).isEmpty()
        }

        @Test
        @DisplayName(
            "does not report excluded annotated function which returns a constant when given \"kotlin.SinceKotlin\""
        )
        fun ignoreAnnotatedFunctionWhichReturnsConstantWhenGivenKotlinSinceKotlin() {
            val config = TestConfig(mapOf(EXCLUDE_ANNOTATED_FUNCTION to "kotlin.SinceKotlin"))
            val rule = FunctionOnlyReturningConstant(config)
            assertThat(rule.compileAndLint(code)).isEmpty()
        }

        @Test
        @DisplayName(
            "does not report excluded annotated function which returns a constant when given listOf(\"kotlin.SinceKotlin\")"
        )
        fun ignoreAnnotatedFunctionWhichReturnsConstantWhenGivenListOfKotlinSinceKotlin() {
            val config = TestConfig(mapOf(EXCLUDE_ANNOTATED_FUNCTION to listOf("kotlin.SinceKotlin")))
            val rule = FunctionOnlyReturningConstant(config)
            assertThat(rule.compileAndLint(code)).isEmpty()
        }
    }

    @Nested
    inner class `FunctionOnlyReturningConstant rule - negative cases` {

        @Test
        fun `does not report functions which do not return constants`() {
            val path = Case.FunctionReturningConstantNegative.path()
            assertThat(subject.lint(path)).isEmpty()
        }
    }
}

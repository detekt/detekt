package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.formatting.wrappers.FunctionSignature
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ListAssert
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * Some test cases were used directly from KtLint to verify the wrapper rule:
 *
 * https://github.com/pinterest/ktlint/blob/dbc7453cd48185772c07b0855c18cb3f5913ed1f/ktlint-ruleset-experimental/src/test/kotlin/com/pinterest/ktlint/ruleset/experimental/FunctionSignatureRuleTest.kt
 */
class FunctionSignatureRuleSpec {
    private fun functionSignatureWrappingRuleAssertThat(
        code: String,
        config: Config = Config.empty
    ): ListAssert<Finding> {
        return assertThat(FunctionSignature(config).lint(code))
    }

    @Test
    fun `Given a single line function signature which is smaller than or equal to the max line length, and the function is followed by a body block, then do no change the signature`() {
        val code =
            """
            fun f(a: Any, b: Any, c: Any): String {
                // body
            }
            """.trimIndent()

        functionSignatureWrappingRuleAssertThat(code)
            .isEmpty()
    }

    @Test
    fun `Given a single line function signature which is greater then the max line length then reformat to a multiline signature`() {
        val code =
            """
            fun f(a: Any, b: Any, c: Any): String {
                // body
            }
            """.trimIndent()

        functionSignatureWrappingRuleAssertThat(
            code,
            TestConfig("maxLineLength" to "10")
        )
            .hasSize(4)
    }

    @Test
    fun `Given a single line function signature and max line length not set but having too many parameters then do reformat as multiline signature`() {
        val code =
            """
            fun f(a: Any, b: Any, c: Any): String {
                // body
            }
            """.trimIndent()

        functionSignatureWrappingRuleAssertThat(
            code,
            TestConfig("forceMultilineWhenParameterCountGreaterOrEqualThan" to "2")
        )
            .hasSize(4)
    }

    @ParameterizedTest(name = "bodyExpressionWrapping: {0}")
    @ValueSource(strings = ["default", "multiline"])
    fun `Given that the function signature and a single line body expression body fit on the same line then do not reformat function signature or body expression`(
        bodyExpressionWrapping: String
    ) {
        val code =
            """
            fun f(a: Any, b: Any): String = "some-result"
            """.trimIndent()

        functionSignatureWrappingRuleAssertThat(
            code,
            TestConfig("functionBodyExpressionWrapping" to bodyExpressionWrapping)
        )
            .isEmpty()
    }

    @ParameterizedTest(name = "bodyExpressionWrapping: {0}")
    @ValueSource(strings = ["always"])
    fun `Given that the function signature and first line of a multiline body expression body fit on the same line then do not reformat the function signature but move the body expression to a separate line`(
        bodyExpressionWrapping: String
    ) {
        val code =
            """
            fun f(a: Any, b: Any): String = "some-result"
            """.trimIndent()

        functionSignatureWrappingRuleAssertThat(
            code,
            TestConfig("functionBodyExpressionWrapping" to bodyExpressionWrapping)
        )
            .hasSize(1)
    }
}

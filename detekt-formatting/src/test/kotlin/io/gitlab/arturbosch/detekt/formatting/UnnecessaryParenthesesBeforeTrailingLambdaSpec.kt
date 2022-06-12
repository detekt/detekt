package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.UnnecessaryParenthesesBeforeTrailingLambda
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * Test cases were used directly from KtLint to verify the wrapper rule:
 *
 * https://github.com/pinterest/ktlint/blob/master/ktlint-ruleset-experimental/src/test/kotlin/com/pinterest/ktlint/ruleset/experimental/UnnecessaryParenthesesBeforeTrailingLambdaRuleTest.kt
 */
class UnnecessaryParenthesesBeforeTrailingLambdaSpec {

    @Test
    fun `reports unnecessary parentheses before trailing lambda`() {
        val code = """
            fun countDash(input: String) =
                "some-string".count() { it == '-' }
        """.trimIndent()
        assertThat(UnnecessaryParenthesesBeforeTrailingLambda(Config.empty).lint(code)).hasSize(1)
    }

    @Test
    fun `does not report unnecessary parentheses before trailing lambda`() {
        val code = """
            fun countDash(input: String) =
                "some-string".count { it == '-' }
        """.trimIndent()
        assertThat(UnnecessaryParenthesesBeforeTrailingLambda(Config.empty).lint(code)).isEmpty()
    }
}

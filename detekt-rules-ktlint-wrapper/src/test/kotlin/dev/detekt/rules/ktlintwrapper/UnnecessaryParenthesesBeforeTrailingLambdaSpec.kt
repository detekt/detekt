package dev.detekt.rules.ktlintwrapper

import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.wrappers.UnnecessaryParenthesesBeforeTrailingLambda
import dev.detekt.test.assertj.assertThat
import org.junit.jupiter.api.Test

/**
 * Test cases were used directly from KtLint to verify the wrapper rule:
 *
 * https://github.com/pinterest/ktlint/blob/0.45.0/ktlint-ruleset-experimental/src/test/kotlin/com/pinterest/ktlint/ruleset/experimental/UnnecessaryParenthesesBeforeTrailingLambdaRuleTest.kt
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

package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.UnnecessaryParenthesesBeforeTrailingLambda
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UnnecessaryParenthesesBeforeTrailingLambdaSpec {

    @Nested
    inner class `UnnecessaryParenthesesBeforeTrailingLambda rule` {

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
}

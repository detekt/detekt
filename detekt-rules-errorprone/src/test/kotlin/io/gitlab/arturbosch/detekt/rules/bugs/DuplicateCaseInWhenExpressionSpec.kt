package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DuplicateCaseInWhenExpressionSpec {
    private val subject = DuplicateCaseInWhenExpression(Config.empty)

    @Nested
    inner class `Duplicate Case In When Expression rule` {

        @Test
        fun `reports duplicated label in when`() {
            val code = """
                fun f() {
                    when (1) {
                        1 -> println()
                        1 -> kotlin.io.println()
                        1, 2 -> println()
                        1, 2 -> kotlin.io.println()
                        else -> println()
                    }
                }"""
            val result = subject.compileAndLint(code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo("When expression has multiple case statements for 1; 1, 2.")
        }

        @Test
        fun `does not report duplicated label in when`() {
            val code = """
                fun f() {
                    when (1) {
                        1 -> println()
                        else -> println()
                    }
                }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
}

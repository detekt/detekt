package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.isThresholded
import org.junit.jupiter.api.Test

class ComplexConditionSpec {

    private val testConfig = TestConfig("allowedComplexity" to 4)

    @Test
    fun `should report complex conditions exceeding allowed complexity`() {
        // given
        val code = """
                    val a = if (5 > 4 && 4 < 6 || (3 < 5 || 2 < 5)) { 42 } else { 24 }
                    
                    fun complexConditions() {
                        while (5 > 4 && 4 < 6 || (3 < 5 || 2 < 5) && 2 % 2 == 0) {}
                        do { } while (5 > 4 && 4 < 6 || (3 < 5 || 2 < 5))
                    }
                    """.trimIndent()

        // when
        val actual = ComplexCondition(testConfig).compileAndLint(code)

        // then
        assertThat(actual).hasSize(1)
        assertThat(actual.first()).isThresholded().withValue(5)
    }

    @Test
    fun `should not report conditions that has exact the allowed complexity`() {
        // given
        val code = """
                    val a = if (5 > 4 && 4 < 6 || (3 < 5 || 2 < 5)) { 42 } else { 24 }
                    
                    fun complexConditions() {
                        while (5 > 4 && 4 < 6 || (3 < 5 || 2 < 5)) {}
                    }
                    """.trimIndent()

        // when
        val actual = ComplexCondition(testConfig).compileAndLint(code)

        // then
        assertThat(actual).isEmpty()
    }

    @Test
    fun `should not report conditions that are below the allowed complexity`() {
        // given
        val code = """
                    fun simpleCondition(): Boolean {
                       return true
                    }
                    """.trimIndent()

        // when
        val actual = ComplexCondition(testConfig).compileAndLint(code)

        // then
        assertThat(actual).isEmpty()
    }
}

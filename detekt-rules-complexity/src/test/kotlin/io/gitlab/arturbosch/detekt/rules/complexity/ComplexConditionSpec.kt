package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class ComplexConditionSpec {

    private val testConfig = TestConfig("allowedConditions" to 4)

    @Test
    fun `should report complex conditions exceeding allowed complexity`() {
        val code = """
           val a = if (5 > 4 && 4 < 6 || (3 < 5 || 2 < 5)) { 42 } else { 24 }
                    
           fun complexConditions() {
               while (5 > 4 && 4 < 6 || (3 < 5 || 2 < 5) && 2 % 2 == 0) {}
               do { } while (5 > 4 && 4 < 6 || (3 < 5 || 2 < 5))
           }
        """.trimIndent()

        val actual = ComplexCondition(testConfig).compileAndLint(code)

        assertThat(actual).hasSize(1)
    }

    @Test
    fun `should not report conditions that has exactly the allowed complexity`() {
        val code = """
            val a = if (5 > 4 && 4 < 6 || (3 < 5 || 2 < 5)) { 42 } else { 24 }
                    
            fun complexConditions() {
                while (5 > 4 && 4 < 6 || (3 < 5 || 2 < 5)) {}
            }
        """.trimIndent()

        val actual = ComplexCondition(testConfig).compileAndLint(code)

        assertThat(actual).isEmpty()
    }

    @Test
    fun `should not report conditions that are below the allowed complexity`() {
        val code = """
             fun simpleCondition(a: Int): Boolean {
                 if(a == 1) {
                     return true
                 }
                 return false
             }
        """.trimIndent()

        val actual = ComplexCondition(testConfig).compileAndLint(code)

        assertThat(actual).isEmpty()
    }
}

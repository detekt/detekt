package dev.detekt.rules.complexity

import dev.detekt.test.TestConfig
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
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

        val actual = ComplexCondition(testConfig).lint(code)

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

        val actual = ComplexCondition(testConfig).lint(code)

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

        val actual = ComplexCondition(testConfig).lint(code)

        assertThat(actual).isEmpty()
    }

    @Test
    fun `should not count logical operators inside line comments`() {
        // The condition has two real operators (&&, ||) so complexity=3, within default threshold of 3.
        // A comment containing && must not push the count to 4.
        val code = """
            fun check(a: Boolean, b: Boolean, c: Boolean): Boolean {
                return if (a && b || c /* && d */) true else false
            }
        """.trimIndent()

        val actual = ComplexCondition(TestConfig("allowedConditions" to 3)).lint(code)

        assertThat(actual).isEmpty()
    }

    @Test
    fun `should not count logical operators inside string literals`() {
        // The condition has two real operators (&&, ||) so complexity=3, within default threshold of 3.
        // The string "&&" must not count toward complexity.
        val code = """
            fun check(a: Boolean, b: Boolean, c: Boolean): Boolean {
                val op = "&&"
                return if (a && b || c) true else false
            }
        """.trimIndent()

        val actual = ComplexCondition(TestConfig("allowedConditions" to 3)).lint(code)

        assertThat(actual).isEmpty()
    }

    @Test
    fun `should still report conditions that are genuinely too complex even without comments`() {
        val code = """
            fun check(a: Boolean, b: Boolean, c: Boolean, d: Boolean): Boolean {
                return if (a && b || c && d) true else false
            }
        """.trimIndent()

        val actual = ComplexCondition(TestConfig("allowedConditions" to 3)).lint(code)

        assertThat(actual).hasSize(1)
    }
}

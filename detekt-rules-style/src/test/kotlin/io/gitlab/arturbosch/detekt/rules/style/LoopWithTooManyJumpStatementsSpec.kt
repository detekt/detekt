package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

private const val MAX_JUMP_COUNT = "maxJumpCount"

class LoopWithTooManyJumpStatementsSpec {

    @Test
    fun `reports for loops with more than 1 break or continue statement`() {
        val code = """
            fun f(i: Int) {
                for (j in 1..2) {
                    if (i > 1) {
                        break
                    } else {
                        continue
                    }
                }
            }
        """.trimIndent()
        assertThat(LoopWithTooManyJumpStatements(Config.empty).compileAndLint(code)).hasTextLocations(20 to 23)
    }

    @Test
    fun `reports while loops with more than 1 break or continue statement`() {
        val code = """
            fun f(i: Int) {
                while (i < 3) {
                    if (i > 1) break else continue
                }
            }
        """.trimIndent()
        assertThat(LoopWithTooManyJumpStatements(Config.empty).compileAndLint(code)).hasTextLocations(20 to 25)
    }

    @Test
    fun `reports do loops with more than 1 break or continue statement`() {
        val code = """
            fun f(i: Int) {
                do {
                    if (i > 2) break else continue
                } while (i < 1)
            }
        """.trimIndent()
        assertThat(LoopWithTooManyJumpStatements(Config.empty).compileAndLint(code)).hasTextLocations(20 to 22)
    }

    @Test
    fun `does not report for loops when max count configuration is set to 2`() {
        val code = """
            fun f(i: Int) {
                for (j in 1..2) {
                    if (i > 1) {
                        break
                    } else {
                        continue
                    }
                }
            }
        """.trimIndent()
        val findings = LoopWithTooManyJumpStatements(TestConfig(MAX_JUMP_COUNT to "2")).compileAndLint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report while loops when max count configuration is set to 2`() {
        val code = """
            fun f(i: Int) {
                while (i < 3) {
                    if (i > 1) break else continue
                }
            }
        """.trimIndent()
        val findings = LoopWithTooManyJumpStatements(TestConfig(MAX_JUMP_COUNT to "2")).compileAndLint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report do loops when max count configuration is set to 2`() {
        val code = """
            fun f(i: Int) {
                do {
                    if (i > 2) break else continue
                } while (i < 1)
            }
        """.trimIndent()
        val findings = LoopWithTooManyJumpStatements(TestConfig(MAX_JUMP_COUNT to "2")).compileAndLint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report for loop with less than 1 break statement`() {
        val code = """
            fun onlyOneJump() {
                for (i in 1..2) {
                    if (i > 1) break
                }
            }
        """.trimIndent()
        val findings = LoopWithTooManyJumpStatements(Config.empty).compileAndLint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report nested loop with less than 1 break or continue statement`() {
        val code = """
            fun jumpsInNestedLoops() {
                for (i in 1..10) {
                    if (i > 5) break
                    // jump statements of the inner loop must not be counted in the outer loop
                    while (i < 3) {
                        if (i > 1) continue
                    }
                }
            }
        """.trimIndent()
        val findings = LoopWithTooManyJumpStatements(Config.empty).compileAndLint(code)
        assertThat(findings).isEmpty()
    }
}

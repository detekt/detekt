package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

private const val MAX_JUMP_COUNT = "maxJumpCount"

class LoopWithTooManyJumpStatementsSpec {
    val subject = LoopWithTooManyJumpStatements()
    private val positiveCode = """
        @Suppress("KotlinConstantConditions", "RedundantSuppression")
        fun tooManyJumpStatements() {
            val i = 0
        
            // reports 1 - too many jump statements
            for (j in 1..2) {
                if (i > 1) {
                    break
                } else {
                    continue
                }
            }
        
            // reports 1 - too many jump statements
            while (i < 2) {
                if (i > 1) break else continue
            }
        
            // reports 1 - too many jump statements
            do {
                if (i > 1) break else continue
            } while (i < 2)
        }
    """.trimIndent()

    @Test
    fun `reports loops with more than 1 break or continue statement`() {
        assertThat(subject.lint(positiveCode)).hasSize(3)
    }

    @Test
    fun `does not report when max count configuration is set to 2`() {
        val config = TestConfig(MAX_JUMP_COUNT to "2")
        val findings = LoopWithTooManyJumpStatements(config).lint(positiveCode)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report loop with less than 1 break or continue statement`() {
        val code = """
            fun onlyOneJump() {
                for (i in 1..2) {
                    if (i > 1) break
                }
            }
            
            fun jumpsInNestedLoops() {
                for (i in 1..2) {
                    if (i > 1) break
                    // jump statements of the inner loop must not be counted in the outer loop
                    @Suppress("KotlinConstantConditions", "RedundantSuppression")
                    while (i > 1) {
                        if (i > 1) continue
                    }
                }
            }
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }
}

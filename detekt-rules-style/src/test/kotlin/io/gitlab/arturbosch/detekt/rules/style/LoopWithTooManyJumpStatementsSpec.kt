package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val MAX_JUMP_COUNT = "maxJumpCount"

class LoopWithTooManyJumpStatementsSpec {
    val subject = LoopWithTooManyJumpStatements()

    @Nested
    inner class `LoopWithTooManyJumpStatements rule` {

        val path = Case.LoopWithTooManyJumpStatementsPositive.path()

        @Test
        fun `reports loops with more than 1 break or continue statement`() {
            assertThat(subject.lint(path)).hasSize(3)
        }

        @Test
        fun `does not report when max count configuration is set to 2`() {
            val config = TestConfig(mapOf(MAX_JUMP_COUNT to "2"))
            val findings = LoopWithTooManyJumpStatements(config).lint(path)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report loop with less than 1 break or continue statement`() {
            val findings = subject.lint(Case.LoopWithTooManyJumpStatementsNegative.path())
            assertThat(findings).isEmpty()
        }
    }
}

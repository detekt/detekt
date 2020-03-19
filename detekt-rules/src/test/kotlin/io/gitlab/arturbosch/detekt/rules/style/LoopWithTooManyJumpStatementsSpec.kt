package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class LoopWithTooManyJumpStatementsSpec : Spek({
    val subject by memoized { LoopWithTooManyJumpStatements() }

    describe("LoopWithTooManyJumpStatements rule") {

        val path = Case.LoopWithTooManyJumpStatementsPositive.path()

        it("reports loops with more than 1 break or continue statement") {
            assertThat(subject.lint(path)).hasSize(3)
        }

        it("does not report when max count configuration is set to 2") {
            val config = TestConfig(mapOf(LoopWithTooManyJumpStatements.MAX_JUMP_COUNT to "2"))
            val findings = LoopWithTooManyJumpStatements(config).lint(path)
            assertThat(findings).isEmpty()
        }

        it("does not report loop with less than 1 break or continue statement") {
            val findings = subject.lint(Case.LoopWithTooManyJumpStatementsNegative.path())
            assertThat(findings).isEmpty()
        }
    }
})

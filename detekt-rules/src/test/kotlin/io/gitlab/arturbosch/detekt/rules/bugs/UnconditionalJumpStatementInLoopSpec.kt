package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnconditionalJumpStatementInLoopSpec : Spek({
    val subject by memoized { UnconditionalJumpStatementInLoop() }

    describe("UnconditionalJumpStatementInLoop rule") {

        it("reports unconditional jumps") {
            val path = Case.UnconditionalJumpStatementInLoopPositive.path()
            assertThat(subject.lint(path)).hasSize(8)
        }

        it("does not report conditional jumps") {
            val path = Case.UnconditionalJumpStatementInLoopNegative.path()
            assertThat(subject.lint(path)).isEmpty()
        }
    }
})

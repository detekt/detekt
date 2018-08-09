package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class UnconditionalJumpStatementInLoopSpec : SubjectSpek<UnconditionalJumpStatementInLoop>({
	subject { UnconditionalJumpStatementInLoop() }

	given("several jump statements in loops") {

		it("reports unconditional jumps") {
			val path = Case.UnconditionalJumpStatementInLoopPositive.path()
			assertThat(subject.lint(path)).hasSize(6)
		}

		it("does not report conditional jumps") {
			val path = Case.UnconditionalJumpStatementInLoopNegative.path()
			assertThat(subject.lint(path)).hasSize(0)
		}
	}
})

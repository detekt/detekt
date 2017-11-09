package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class LoopWithTooManyJumpStatementsSpec : SubjectSpek<LoopWithTooManyJumpStatements>({
	subject { LoopWithTooManyJumpStatements() }

	given("loops with multiple break or continue statements") {

		val path = Case.LoopWithTooManyJumpStatementsPositive.path()

		it("reports loops with more than 1 break or continue statement") {
			assertThat(subject.lint(path)).hasSize(3)
		}

		it("does not report when max count configuration is set to 2") {
			val config = TestConfig(mapOf(LoopWithTooManyJumpStatements.MAX_JUMP_COUNT to "2"))
			val findings = LoopWithTooManyJumpStatements(config).lint(path)
			assertThat(findings).hasSize(0)
		}

		it("reports loops with more than 1 break or continue statement") {
			assertThat(subject.lint(Case.LoopWithTooManyJumpStatementsNegative.path())).hasSize(0)
		}
	}
})

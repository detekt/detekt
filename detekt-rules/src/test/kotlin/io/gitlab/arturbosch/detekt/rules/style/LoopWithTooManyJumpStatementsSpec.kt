package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class LoopWithTooManyJumpStatementsSpec : SubjectSpek<LoopWithTooManyJumpStatements>({
	subject { LoopWithTooManyJumpStatements() }

	given("loops with multiple break or continue statements") {

		it("reports loops with more than 1 break or continue statement") {
			val path = Case.LoopWithTooManyJumpStatements.path()
			assertThat(subject.lint(path)).hasSize(3)
		}
	}
})

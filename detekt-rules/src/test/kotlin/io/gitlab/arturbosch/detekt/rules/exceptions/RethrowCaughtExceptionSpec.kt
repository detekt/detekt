package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class RethrowCaughtExceptionSpec : SubjectSpek<RethrowCaughtException>({
	subject { RethrowCaughtException() }

	given("multiple caught exceptions") {

		it("reports caught exceptions which are rethrown") {
			val path = Case.RethrowCaughtExceptionPositive.path()
			assertThat(subject.lint(path)).hasSize(4)
		}

		it("does not report caught exceptions which are encapsulated in another exception or logged") {
			val path = Case.RethrowCaughtExceptionNegative.path()
			assertThat(subject.lint(path)).hasSize(0)
		}
	}
})

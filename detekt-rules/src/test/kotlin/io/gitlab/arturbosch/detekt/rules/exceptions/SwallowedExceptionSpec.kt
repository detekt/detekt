package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class SwallowedExceptionSpec : SubjectSpek<SwallowedException>({
	subject { SwallowedException() }

	given("several catch blocks") {

		it("reports swallowed exceptions") {
			assertThat(subject.lint(Case.SwallowedExceptionPositive.path())).hasSize(3)
		}

		it("does not report thrown catch blocks") {
			assertThat(subject.lint(Case.SwallowedExceptionNegative.path())).hasSize(0)
		}
	}
})

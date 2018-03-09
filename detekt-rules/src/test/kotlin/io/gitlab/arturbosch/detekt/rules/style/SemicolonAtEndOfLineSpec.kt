package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class SemicolonAtEndOfLineSpec : SubjectSpek<SemicolonAtEndOfLine>({
	subject { SemicolonAtEndOfLine() }

	given("a file that has lines that end with a semicolon") {
		it("should flag it") {
			assertThat(subject.lint(Case.SemicolonAtEndOfLinePositive.path())).hasSize(2)
		}
	}

	given("a file that does not have lines that end with a semicolon") {
		it("should not flag it") {
			assertThat(subject.lint(Case.SemicolonAtEndOfLineNegative.path())).hasSize(0)
		}
	}
})

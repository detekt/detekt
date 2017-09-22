package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class NewLineAtEndOfFileSpec : SubjectSpek<NewLineAtEndOfFile>({
	subject { NewLineAtEndOfFile() }

	given("a kt file containing new space at end") {
		it("should not flag it") {
	  		assertThat(subject.lint(Case.NewLineAtEndOfFile.path())).hasSize(0)
		}
  	}

	given("a kt file not containing new space at end") {
		it("should flag it") {
			assertThat(subject.lint("class Test")).hasSize(1)
		}
	}

	given("an empty kt file") {
		it("should not flag it") {
			assertThat(subject.lint(Case.EmptyKtFile.path())).hasSize(0)
		}
	}
})

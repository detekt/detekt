package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class EqualsAlwaysReturnsTrueOrFalseSpec : SubjectSpek<EqualsAlwaysReturnsTrueOrFalse>({
	subject { EqualsAlwaysReturnsTrueOrFalse(Config.empty) }

	describe("check if equals() method always returns true or false") {

		it("returns constant boolean") {
			val file = compileForTest(Case.EqualsAlwaysReturnsConstant.path())
			Assertions.assertThat(subject.lint(file.text)).hasSize(2)
		}
	}
})

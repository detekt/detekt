package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class ExceptionRaisedInUnexpectedLocationSpec : SubjectSpek<ExceptionRaisedInUnexpectedLocation>({
	subject { ExceptionRaisedInUnexpectedLocation() }

	given("methods which are not expected to throw exceptions") {

		it("reports the methods raising an unexpected exception") {
			val file = compileForTest(Case.ExceptionRaisedInMethods.path())
			val findings = subject.lint(file.text)
			Assertions.assertThat(findings).hasSize(7)
		}
	}
})

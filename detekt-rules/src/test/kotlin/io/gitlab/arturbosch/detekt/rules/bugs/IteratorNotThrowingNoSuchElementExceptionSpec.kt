package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class IteratorNotThrowingNoSuchElementExceptionSpec : SubjectSpek<IteratorNotThrowingNoSuchElementException>({
	subject { IteratorNotThrowingNoSuchElementException() }

	given("two iterator classes which next() method do not throw a NoSuchElementException") {

		it("should report") {
			val file = compileForTest(Case.IteratorImpl.path())
			val findings = subject.lint(file.text)
			Assertions.assertThat(findings).hasSize(3)
		}
	}
})

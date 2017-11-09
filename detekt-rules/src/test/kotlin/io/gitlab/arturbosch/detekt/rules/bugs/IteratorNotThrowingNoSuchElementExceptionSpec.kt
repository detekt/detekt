package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class IteratorNotThrowingNoSuchElementExceptionSpec : SubjectSpek<IteratorNotThrowingNoSuchElementException>({
	subject { IteratorNotThrowingNoSuchElementException() }

	given("two iterator classes which next() method do not throw a NoSuchElementException") {

		it("reports invalid next() implementations") {
			val path = Case.IteratorImplViolations.path()
			assertThat(subject.lint(path)).hasSize(4)
		}

		it("does not report correct next() implemenations") {
			val path = Case.IteratorImpl.path()
			assertThat(subject.lint(path)).hasSize(0)
		}
	}
})

package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class IteratorHasNextCallsNextMethodSpec : SubjectSpek<IteratorHasNextCallsNextMethod>({
	subject { IteratorHasNextCallsNextMethod() }

	given("some iterator classes with a hasNext() method which calls next() method") {

		it("reports wrong iterator implementation") {
			val path = Case.IteratorImplPositive.path()
			assertThat(subject.lint(path)).hasSize(4)
		}

		it("does not report correct iterator implementations") {
			val path = Case.IteratorImplNegative.path()
			assertThat(subject.lint(path)).hasSize(0)
		}
	}
})

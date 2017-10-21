package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class IteratorHasNextCallsNextMethodSpec : SubjectSpek<IteratorHasNextCallsNextMethod>({
	subject { IteratorHasNextCallsNextMethod() }

	given("two iterator classes with a hasNext() method which calls next() method") {

		it("should report") {
			val findings = subject.lint(compileForTest(Case.IteratorImpl.path()).text)
			assertThat(findings).hasSize(3)
		}
	}
})

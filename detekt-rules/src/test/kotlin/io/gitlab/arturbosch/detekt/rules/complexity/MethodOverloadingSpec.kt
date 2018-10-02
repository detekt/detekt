package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class MethodOverloadingSpec : SubjectSpek<MethodOverloading>({

	subject { MethodOverloading(threshold = 3) }

	given("several overloaded methods") {

		it("reports overloaded methods which exceed the threshold") {
			subject.lint(Case.OverloadedMethods.path())
			assertThat(subject.findings.size).isEqualTo(3)
		}

		it("does not report overloaded methods which do not exceed the threshold") {
			subject.lint("""
				class Test {
					fun x() { }
					fun x(i: Int) { }
				}""")
			assertThat(subject.findings.size).isZero()
		}
	}
})

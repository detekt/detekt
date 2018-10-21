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

		val findings = subject.lint(Case.OverloadedMethods.path())

		it("reports overloaded methods which exceed the threshold") {
			assertThat(findings.size).isEqualTo(3)
		}

		it("reports the correct method name") {
			val expected = "The method 'overloadedMethod' is overloaded 3 times."
			assertThat(findings[0].message).isEqualTo(expected)
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

	given("several overloaded extensions functions") {

		it("does not report extension methods with a different receiver") {
			subject.lint("""
				fun Boolean.foo() {}
				fun Int.foo() {}
				fun Long.foo() {}""")
			assertThat(subject.findings.size).isZero()
		}

		it("reports extension methods with the same receiver") {
			subject.lint("""
				fun Int.foo() {}
				fun Int.foo(i: Int) {}
				fun Int.foo(i: String) {}""")
			assertThat(subject.findings.size).isEqualTo(1)
		}
	}
})

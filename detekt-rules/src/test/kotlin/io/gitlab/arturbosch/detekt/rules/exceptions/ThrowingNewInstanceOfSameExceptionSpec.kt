package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class ThrowingNewInstanceOfSameExceptionSpec : SubjectSpek<ThrowingNewInstanceOfSameException>({
	subject { ThrowingNewInstanceOfSameException() }

	given("a catch block which rethrows a new instance of the caught exception") {
		val code = """
			fun x() {
				try {
				} catch (e: IllegalStateException) {
					throw IllegalStateException(e)
				}
			}
		"""

		it("should report") {
			val findings = subject.lint(code)
			Assertions.assertThat(findings).hasSize(1)
		}
	}

	given("a catch block which rethrows a new instance of another exception") {
		val code = """
			fun x() {
				try {
				} catch (e: IllegalStateException) {
					throw IllegalArgumentException(e)
				}
			}
		"""

		it("should not report") {
			val findings = subject.lint(code)
			Assertions.assertThat(findings).hasSize(0)
		}
	}

	given("a catch block which throws a new instance of the same exception type without wrapping the caught exception") {
		val code = """
			fun x() {
				try {
				} catch (e: IllegalStateException) {
					throw IllegalStateException()
				}
			}
		"""

		it("should not report") {
			val findings = subject.lint(code)
			Assertions.assertThat(findings).hasSize(0)
		}
	}
})

package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class ReturnFromFinallySpec : SubjectSpek<ReturnFromFinally>({
	subject { ReturnFromFinally() }

	given("a finally block with a return statement") {
		val code = """
			fun x() {
				try {
				} finally {
					return 0
				}
			}
		"""

		it("should report") {
			val findings = subject.lint(code)
			Assertions.assertThat(findings).hasSize(1)
		}
	}

	given("a finally block with no return statement") {
		val code = """
			fun x() {
				try {
				} finally {
				}
			}
		"""

		it("should not report") {
			val findings = subject.lint(code)
			Assertions.assertThat(findings).hasSize(0)
		}
	}

	given("a finally block with a nested return statement") {
		val code = """
			fun x() {
				try {
				} finally {
					if (1 == 1) {
						return 0
					}
				}
			}
		"""

		it("should report") {
			val findings = subject.lint(code)
			Assertions.assertThat(findings).hasSize(1)
		}
	}

	given("a finally block with a return in an inner function") {
		val code = """
			fun x() {
				try {
				} finally {
					fun y() {
						return
					}
					y()
				}
			}
		"""

		it("should not report") {
			val findings = subject.lint(code)
			Assertions.assertThat(findings).hasSize(0)
		}
	}
})

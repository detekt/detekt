package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class RethrowCaughtExceptionSpec : SubjectSpek<RethrowCaughtException>({
	subject { RethrowCaughtException() }

	given("a caught exception rethrown") {
		val code = """
			function x() {
				try {
				} catch (e: MyException) {
					throw e
				}
			}
		"""

		it("should report") {
			val findings = subject.lint(code)
			Assertions.assertThat(findings).hasSize(1)
		}
	}

	given("a new exception thrown") {
		val code = """
			function x() {
				try {
				} catch (e: MyException) {
					throw MyException2(e)
				}
			}
		"""

		it("should not report") {
			val findings = subject.lint(code)
			Assertions.assertThat(findings).hasSize(0)
		}
	}
})

package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class ThrowingExceptionFromFinallySpec : SubjectSpek<ThrowingExceptionFromFinally>({
	subject { ThrowingExceptionFromFinally() }

	given("some finally blocks") {

		it("should report a throw expression") {
			val code = """
				fun x() {
					try {
					} finally {
						if (1 == 1) {
							throw IllegalArgumentException()
						}
					}
				}"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("should report a nested throw expression") {
			val code = """
				fun x() {
					try {
					} finally {
						throw IllegalArgumentException()
					}
				}"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("should not report a finally expression without a throw expression") {
			val code = """
				fun x() {
					try {
					} finally {
						println()
					}
				}"""
			assertThat(subject.lint(code)).hasSize(0)
		}
	}
})

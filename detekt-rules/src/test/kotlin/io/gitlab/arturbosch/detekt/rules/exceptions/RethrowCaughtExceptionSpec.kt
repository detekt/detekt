package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class RethrowCaughtExceptionSpec : SubjectSpek<RethrowCaughtException>({
	subject { RethrowCaughtException() }

	given("some caught exceptions that are rethrown") {

		it("should report a rethrown exception") {
			val code = """
				fun x() {
					try {
					} catch (e: IllegalStateException) {
						throw e
					}
				}"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("should report a rethrown exception with trailing (dead) code") {
			val code = """
				fun x() {
					try {
					} catch (e: IllegalStateException) {
						throw e
						print("log")
					}
				}"""
			assertThat(subject.lint(code)).hasSize(1)
		}
	}

	given("a caught exception that is encapsulated in a new exception and thrown") {

		it("should not report an encapsulated exception") {
			val code = """
				fun x() {
					try {
					} catch (e: IllegalStateException) {
						throw IllegalArgumentException(e)
					}
				}"""
			assertThat(subject.lint(code)).hasSize(0)
		}
	}

	given("a caught exception that is logged") {

		it("should not report a logged exception") {
			val code = """
				fun x() {
					try {
					} catch (e: IllegalStateException) {
						print("log")
					}
				}"""
			assertThat(subject.lint(code)).hasSize(0)
		}
	}

	given("a caught exception that is rethrown after doing something") {

		it("should not report a thrown exception after logging") {
			val code = """
				fun x() {
					try {
					} catch (e: IllegalStateException) {
						print("log")
						throw e
					}
				}"""
			assertThat(subject.lint(code)).hasSize(0)
		}
	}
})

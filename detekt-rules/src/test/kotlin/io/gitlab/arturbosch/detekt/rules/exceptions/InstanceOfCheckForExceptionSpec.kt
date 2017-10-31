package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class InstanceOfCheckForExceptionSpec : SubjectSpek<InstanceOfCheckForException>({
	subject { InstanceOfCheckForException() }

	given("several catch blocks") {

		it("has is and as checks") {
			val code = """
				fun x() {
					try {
					} catch(e: IOException) {
						if (e is MyException || (e as MyException) != null) {
							return
						}
					}
				}
				"""
			assertThat(subject.lint(code)).hasSize(2)
		}

		it("has nested is and as checks") {
			val code = """
				fun x() {
					try {
					} catch(e: IOException) {
						if (1 == 1) {
							val b = e !is MyException || (e as MyException) != null
						}
					}
				}
				"""
			assertThat(subject.lint(code)).hasSize(2)
		}

		it("has no instance of check") {
			val code = """
				fun x() {
					try {
					} catch(e: IOException) {
					}
				}
				"""
			assertThat(subject.lint(code)).hasSize(0)
		}
	}
})

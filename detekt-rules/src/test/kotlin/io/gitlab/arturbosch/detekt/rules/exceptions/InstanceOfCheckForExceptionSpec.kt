package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class InstanceOfCheckForExceptionSpec : Spek({
    val subject by memoized { InstanceOfCheckForException() }

    describe("InstanceOfCheckForException rule") {

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
						val s = ""
						if (s is String || (s as String) != null) {
							val b = s !is MyException || (s as MyException) != null
						}
					}
				}
				"""
            assertThat(subject.lint(code)).hasSize(0)
        }
    }
})

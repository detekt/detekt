package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class EqualsAlwaysReturnsTrueOrFalseSpec : SubjectSpek<EqualsAlwaysReturnsTrueOrFalse>({
	subject { EqualsAlwaysReturnsTrueOrFalse(Config.empty) }

	describe("check if equals() method always returns true or false") {

		it("reports returning 'true'") {
			val code = """
				class A {
					override fun equals(other: Any?): Boolean {
						return true
					}
				}"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("reports returning 'false'") {
			val code = """
				class A {
					override fun equals(other: Any?): Boolean {
						return false
					}
				}"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("reports returning 'false' in method body without curly braces") {
			val code = """
				class A {
					override fun equals(other: Any?) = false
				}"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("reports returning 'false' with unreachable second return") {
			val code = """
				class A {
					override fun equals(other: Any?): Boolean {
						return false
						return true
					}
				}
				"""
			assertThat(subject.lint(code)).hasSize(1)
		}



		it("reports returning 'false' with unreachable statements after it") {
			val code = """
				class A {
					override fun equals(other: Any?): Boolean {
						return false
						val i = 0
					}
				}
				"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("does not report returning a constant whilst another return statement") {
			val code = """
				class A {
					override fun equals(other: Any?): Boolean {
						if (other is A) {
							return true
						}
						return false
					}
				}"""
			assertThat(subject.lint(code)).hasSize(0)
		}

		it("does not report returning no constant'") {
			val code = """
				class A {
					override fun equals(other: Any?): Boolean {
						return this == other
					}
				}"""
			assertThat(subject.lint(code)).hasSize(0)
		}

		it("does not report global equals function") {
			val code = """
				fun equals(other: Any?): Boolean {
					return false;
				}"""
			assertThat(subject.lint(code)).hasSize(0)
		}

		it("does not report equals function with wrong name") {
			val code = """
				class A {
					fun equal(other: Any?): Boolean {
						return true
					}
				}"""
			assertThat(subject.lint(code)).hasSize(0)
		}

		it("does not report equals function with wrong parameter type") {
			val code = """
				class A {
					fun equals(other: Any): Boolean {
						return true
					}
				}"""
			assertThat(subject.lint(code)).hasSize(0)
		}
	}
})

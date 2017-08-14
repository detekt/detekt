package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class EqualsAlwaysReturnsTrueOrFalseSpec : SubjectSpek<EqualsAlwaysReturnsTrueOrFalse>({
	subject { EqualsAlwaysReturnsTrueOrFalse(Config.empty) }

	describe("check if equals() method always returns true or false") {

		it("returns constant boolean") {
			val code = """
				class A {
					override fun equals(other: Any?): Boolean {
						return true
					}
				}

				class B {
					override fun equals(other: Any?): Boolean {
						return false
					}
				}"""
			Assertions.assertThat(subject.lint(code)).hasSize(2)
		}
	}
})

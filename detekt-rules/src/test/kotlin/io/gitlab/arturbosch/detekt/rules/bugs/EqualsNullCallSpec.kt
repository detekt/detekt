package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class EqualsNullCallSpec : SubjectSpek<EqualsNullCall>({
	subject { EqualsNullCall(Config.empty) }

	given("an equals method with a parameter") {

		it("with null as parameter") {
			val code = """
				fun x(a: String) {
					a.equals(null)
				}"""
			Assertions.assertThat(subject.lint(code).size).isEqualTo(1)
		}

		it("with nested equals(null) call as parameter") {
			val code = """
				fun x(a: String, b: String) {
					a.equals(b.equals(null))
				}"""
			Assertions.assertThat(subject.lint(code).size).isEqualTo(1)
		}

		it("with non-nullable parameter") {
			val code = """
				fun x(a: String, b: String) {
					a.equals(b)
				}"""
			Assertions.assertThat(subject.lint(code).size).isEqualTo(0)
		}
	}
})

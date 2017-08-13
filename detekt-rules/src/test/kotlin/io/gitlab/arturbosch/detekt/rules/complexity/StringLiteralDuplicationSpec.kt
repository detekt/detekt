package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class StringLiteralDuplicationSpec : SubjectSpek<StringLiteralDuplication>({
	subject { StringLiteralDuplication() }

	given("many hardcoded strings") {

		it("reports 3 equal hardcoded strings") {
			val code = """
				class Duplication {
					var s1 = "Foo"
					fun f(s: String = "Foo") {
						s1.equals("Foo")
					}
				}"""
			Assertions.assertThat(subject.lint(code).size).isEqualTo(1)
		}

		it("does not report 2 equal hardcoded strings") {
			val code = """
				var s1 = "Foo"
				var s2 = "Foo"
				var s3 = "Bar"
				var s4 = ""
			"""
			Assertions.assertThat(subject.lint(code).size).isEqualTo(0)
		}
	}
})

package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class OptionalWhenBracesSpec : SubjectSpek<OptionalWhenBraces>({
	subject { OptionalWhenBraces() }

	describe("check optional braces in when expression") {

		it("has unnecessary braces") {
			val code = """
				fun x() {
					when (1) {
						1 -> { foo() }
						2 -> foo
						3 -> {
							foo()
							bar()
						}
					}
				}"""
			Assertions.assertThat(subject.lint(code)).hasSize(1)
		}
	}
})

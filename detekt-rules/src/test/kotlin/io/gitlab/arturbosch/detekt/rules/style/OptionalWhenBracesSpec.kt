package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class OptionalWhenBracesSpec : SubjectSpek<OptionalWhenBraces>({
    subject { OptionalWhenBraces() }

    describe("check optional braces in when expression") {

        it("has necessary braces") {
            val code = """
				fun x() {
					when (1) {
						1 -> foo
						2 -> {
							foo()
							bar()
						}
						else -> {
							// a comment
							foo()
						}
					}
				}"""
            assertThat(subject.lint(code)).hasSize(0)
        }

        it("has unnecessary braces") {
            val code = """
				fun x() {
					when (1) {
						1 -> { foo() }
						else -> bar()
					}
				}"""
            assertThat(subject.lint(code)).hasSize(1)
        }
    }
})

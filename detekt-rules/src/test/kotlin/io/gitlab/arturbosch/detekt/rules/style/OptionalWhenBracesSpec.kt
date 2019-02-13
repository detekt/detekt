package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class OptionalWhenBracesSpec : Spek({
    val subject by memoized { OptionalWhenBraces() }

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

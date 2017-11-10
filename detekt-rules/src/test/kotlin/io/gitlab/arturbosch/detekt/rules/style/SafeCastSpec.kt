package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class SafeCastSpec : SubjectSpek<SafeCast>({
	subject { SafeCast() }

	given("some cast expressions") {

		it("reports negated expression") {
			val code = """
				fun test() {
					if (element !is KtElement) {
						null
					} else {
						element
					}
				}"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("reports expression") {
			val code = """
				fun test() {
					if (element is KtElement) {
						element
					} else {
						null
					}
				}"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("does not report wrong condition") {
			val code = """
				fun test() {
					if (element == other) {
						element
					} else {
						null
					}
				}"""
			assertThat(subject.lint(code)).hasSize(0)
		}

		it("does not report wrong else clause") {
			val code = """
				fun test() {
					if (element is KtElement) {
						element
					} else {
						KtElement()
					}
				}"""
			assertThat(subject.lint(code)).hasSize(0)
		}
	}
})

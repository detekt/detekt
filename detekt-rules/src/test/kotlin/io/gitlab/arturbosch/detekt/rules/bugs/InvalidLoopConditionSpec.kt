package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class InvalidLoopConditionSpec : SubjectSpek<InvalidLoopCondition>({
	subject { InvalidLoopCondition(Config.empty) }

	describe("check for loop conditions") {

		it("does not report correct bounds in for loop conditions") {
			val code = """
				fun f() {
					for (i in 2..2) {}
					for (i in 2 downTo 2) {}
					for (i in 2 until 2) {}
					for (i in (1+1)..3) { }
				}"""
			assertThat(subject.lint(code)).hasSize(0)
		}

		it("reports incorrect bounds in for loop conditions") {
			val code = """
				fun f() {
					for (i in 2..1) { }
					for (i in 1 downTo 2) { }
					for (i in 2 until 1) { }
				}"""
			assertThat(subject.lint(code)).hasSize(3)
		}

		it("reports nested loops with incorrect bounds in for loop conditions") {
			val code = """
				fun f() {
					for (i in 2..2) {
						for (i in 2..1) { }
					}
				}"""
			assertThat(subject.lint(code)).hasSize(1)
		}
	}
})

package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class UntilInsteadOfRangeToSpec : SubjectSpek<UntilInsteadOfRangeTo> ({
	subject { UntilInsteadOfRangeTo(Config.empty) }

	it("does not report if rangeTo not used") {
		val code = """
				fun f() {
					for (i in 0 until 10 - 1) {}
					for (i in 10 downTo 2 - 1) {}
				}"""
		assertThat(subject.lint(code)).hasSize(0)
	}

	it("does not report if upper value isn't a binary expression") {
		val code = """
				fun f() {
					for (i in 0 .. 10) {}
				}"""
		assertThat(subject.lint(code)).hasSize(0)
	}

	it("does not report if not minus one") {
		val code = """
				fun f() {
					for (i in 0 .. 10 + 1) {}
					for (i in 0 .. 10 - 2) {}
				}"""
		assertThat(subject.lint(code)).hasSize(0)
	}

	it("reports for both 'rangeTo' and '..'") {
		val code = """
				fun f() {
					for (i in 0 .. 10 - 1) {}
					for (i in 0 rangeTo 10 - 1) {}
				}"""
		assertThat(subject.lint(code)).hasSize(2)
	}
})

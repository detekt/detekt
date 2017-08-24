package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class UselessIncrementSpec : SubjectSpek<UselessIncrement>({
	subject { UselessIncrement() }

	describe("check several types of postfix increments") {

		it("overrides the incremented integer") {
			val code = """
				fun x() {
					var i = 0
					var j = 0
					j = i++
					i = i++
					i = 1 + i++
					i = i++ + 1
				}"""
			Assertions.assertThat(subject.lint(code)).hasSize(3)
		}

		it("returns no incremented value") {
			val code = """
				fun x() {
					var i = 0
					if (i == 0) return 1 + j++
					return i++
				}"""
			Assertions.assertThat(subject.lint(code)).hasSize(2)
		}
	}
})

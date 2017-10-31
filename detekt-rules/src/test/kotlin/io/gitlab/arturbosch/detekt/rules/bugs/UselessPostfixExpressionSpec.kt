package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import org.assertj.core.api.Assertions.assertThat

class UselessPostfixExpressionSpec : SubjectSpek<UselessPostfixExpression>({
	subject { UselessPostfixExpression() }

	describe("check several types of postfix increments") {

		it("overrides the incremented integer") {
			val code = """
				fun x() {
					var i = 0
					var j = 0
					j = i++ // invalid
					i = i-- // invalid
					i = 1 + i++ // invalid
					i = i++ + 1
				}"""
			assertThat(subject.lint(code)).hasSize(3)
		}

		it("returns no incremented value") {
			val code = """
				fun x() {
					var i = 0
					if (i == 0) return 1 + j++
					return i++
				}"""
			assertThat(subject.lint(code)).hasSize(2)
		}
	}
})

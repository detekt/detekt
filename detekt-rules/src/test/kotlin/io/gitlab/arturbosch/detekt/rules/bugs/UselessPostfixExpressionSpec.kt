package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

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

		it("should not report field increments") {
			val code = """
				class Test {
					private var runningId: Long = 0

					fun increment() {
						runningId++
					}

					fun getId(): Long {
						return runningId++
					}
				}
				"""
			assertThat(subject.lint(code)).isEmpty()
		}

		it("should detect properties shadowing fields that are incremented") {
			val code = """
				class Test {
					private var runningId: Long = 0

					fun getId(): Long {
						val runningId: Long = 0
						return runningId++
					}
				}
				"""
			assertThat(subject.lint(code)).hasSize(1)
		}
	}

	describe("Only ++ and -- postfix operators should be considered") {

		it("should not report !! in a return statement") {
			val code = """
				fun getInstance(): SwiftBrowserIdleTaskHelper {
					return sInstance!!
				}
				"""
			assertThat(subject.lint(code)).isEmpty()
		}

		it("should not report !! in a standalone expression") {
			assertThat(subject.lint("sInstance!!")).isEmpty()
		}
	}
})

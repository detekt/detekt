package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author aballano
 */
class BodyExpressionSpec : SubjectSpek<BodyExpression>({

	subject { BodyExpression() }

	describe("a simple test") {

		it("should find functions with return statements that can be body expressions") {
			val findings = subject.lint(code)
			Assertions.assertThat(findings).hasSize(2)
		}
	}

})

private val code: String =
		"""
			class BodyExpression {

				fun getExclamation(): String = "!"

				fun getGreeting(): String {
					return "hello"
				}

				fun getSubject(): String {
					return "world"
				}

				fun getBoth(): String {
					val both = getGreeting() + " " + getSubject()"
					return both
				}

				fun print() {
					println(getBoth())
				}
			}
		"""


package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 */
class DuplicateCaseInWhenExpressionSpec : SubjectSpek<DuplicateCaseInWhenExpression>({
	subject { DuplicateCaseInWhenExpression(Config.empty) }

	given("several when expressions") {

		it("reports duplicated label in when") {
			val code = """
				fun f() {
					when (1) {
						1 -> println()
						1 -> println()
						else -> println()
					}
				}"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("reports duplicated label in when") {
			val code = """
				fun f() {
					when (1) {
						1 -> println()
						else -> println()
					}
				}"""
			assertThat(subject.lint(code)).hasSize(0)
		}
	}
})

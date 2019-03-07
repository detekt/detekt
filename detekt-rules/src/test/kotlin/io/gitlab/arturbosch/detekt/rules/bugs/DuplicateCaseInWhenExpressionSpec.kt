package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Artur Bosch
 */
class DuplicateCaseInWhenExpressionSpec : Spek({
    val subject by memoized { DuplicateCaseInWhenExpression(Config.empty) }

    describe("Duplicate Case In When Expression rule") {

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

        it("does not report duplicated label in when") {
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

package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class OptionalUnitSpec : Spek({

	describe("running specified rule") {

		it("should detect one finding") {
			val findings = OptionalUnit().lint("""
				fun returnsUnit1(): Unit {
				}

				fun returnsUnit2() = Unit
			""")
			assertThat(findings).hasSize(2)
		}

		it("should not report Unit reference") {
			val findings = OptionalUnit().lint("""
				fun returnsNothing() {
					Unit
				}
			""")
			assertThat(findings).isEmpty()
		}
	}
})

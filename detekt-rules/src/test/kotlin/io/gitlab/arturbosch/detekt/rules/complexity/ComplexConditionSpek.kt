package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class ComplexConditionSpec : Spek({

	given("some complex conditions") {

		val code = """
			val a = if (5 > 4 && 4 < 6 || (3 < 5 || 2 < 5)) { 42 } else { 24 }

			fun complexConditions() {
				while (5 > 4 && 4 < 6 || (3 < 5 || 2 < 5)) {}
				do { } while (5 > 4 && 4 < 6 || (3 < 5 || 2 < 5))
			}
		"""

		it("reports some complex conditions") {
			assertThat(ComplexCondition().lint(code)).hasSize(3)
		}
	}
})

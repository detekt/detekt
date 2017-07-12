package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
internal class ComplexConditionTest {

	private val code = """
		val a = if (5 > 4 && 4 < 6 || (3 < 5 || 2 < 5)) { 42 } else { 24 }

		fun complexConditions() {
			while (5 > 4 && 4 < 6 || (3 < 5 || 2 < 5)) {}
			do { } while (5 > 4 && 4 < 6 || (3 < 5 || 2 < 5))
		}
	"""

	@Test
	fun findOneComplexCondition() {
		assertThat(ComplexCondition().lint(code)).hasSize(3)
	}
}

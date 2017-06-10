package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.rules.load
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MaxLineLengthSpec {

	val root = load(Case.MaxLineLength)

	@Test
	fun findMaxLineLengthViolationsWithDefault() {
		find(6) { MaxLineLength() }
	}

	@Test
	fun findMaxLineLengthViolationsWithConfig() {
		find(0) { MaxLineLength(TestConfig(mapOf("maxLineLength" to "200"))) }
	}

	private fun find(expected: Int, block: () -> Rule) {
		val rule = block()
		rule.visit(root)
		assertThat(rule.findings).hasSize(expected)
	}

}

package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
class EmptyIfBlockTest {

	private val rule = EmptyIfBlock(Config.empty)

	@Test
	fun testPositiveCases() {
		test(Case.EmptyIfPositive, 4)
	}

	@Test
	fun testNegativeCases() {
		test(Case.EmptyIfNegative, 0)
	}

	private fun test(case: Case, size: Int) {
		rule.lint(compileForTest(case.path()).text)
		assertThat(rule.findings).hasSize(size)
	}
}

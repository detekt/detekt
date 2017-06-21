package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.test.RuleTest
import io.gitlab.arturbosch.detekt.test.format
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
class OptionalReturnKeywordTest : RuleTest {

	override val rule: Rule = OptionalReturnKeyword(Config.empty)

	@Test
	fun removeInSimpleIfStatement() {
		val actual = "val z = if (true) return x else return y"
		val expected = "val z = if (true) x else y"

		assertThat(rule.format(actual)).isEqualTo(expected)
	}

	@Test
	fun removeInComplexIfStatement() {
		val actual = "val z = if (true) return if (true) { if (false) return b; return x } else a else return y"
		val expected = "val z = if (true) if (true) { if (false) return b; x } else a else y"

		assertThat(rule.lint(actual).size).isEqualTo(3)
		assertThat(rule.format(actual)).isEqualTo(expected)
	}
}
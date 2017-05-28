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
class SpacingAfterCommaTest : RuleTest {

	override val rule: Rule = SpacingAfterComma(Config.empty)

	@Test
	fun findFourSpacesAfterSemicolonsAndCommas() {
		assertThat(rule.lint("fun main() { x(1,3);x(1,3);println(\",;\") }")).hasSize(4)
		assertThat(rule.lint("enum class E { A,B,C }")).hasSize(2)
	}

	@Test
	fun noSpacesWithinStrings() {
		assertThat(rule.lint("fun main() { println(\",;,,,,;;\") }")).isEmpty()
	}

	@Test
	fun spacesAfterSemicolonsAndCommas() {
		assertThat(rule.format("fun main() { x(1,3);x(1,3) }")).isEqualTo("fun main() { x(1, 3); x(1, 3) }")
	}
}
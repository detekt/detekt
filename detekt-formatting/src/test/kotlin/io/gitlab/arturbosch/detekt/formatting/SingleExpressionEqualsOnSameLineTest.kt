package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.test.RuleTest
import io.gitlab.arturbosch.detekt.test.format
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
internal class SingleExpressionEqualsOnSameLineTest : RuleTest {

	override val rule: Rule = SingleExpressionEqualsOnSameLine(Config.Companion.empty)

	@Test
	fun testLint() {
		Assertions.assertThat(rule.lint(
				"""
fun stuff() =
 	5
fun stuff2() {
	5
}
""")).hasSize(1)
	}

	@Test
	fun testFormat() {
		Assertions.assertThat(rule.format(
				"""
fun stuff() =
 	5

""")).isEqualTo("fun stuff() = 5")
	}

}
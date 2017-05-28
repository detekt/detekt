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
class TrailingSpacesTest : RuleTest {

	override val rule: Rule = TrailingSpaces(Config.empty)

	@Test
	fun testLint() {
		assertThat(rule.lint("fun main() {\n    val a = 1\n\n \n} ")).hasSize(2)
	}

	@Test
	fun testFormat() {
		assertThat(rule.format("fun main() {   \n    val a = 1 \n  \n \n    call()\n} \n\n"))
				.isEqualTo("fun main() {\n    val a = 1\n\n\n    call()\n}\n")
	}

}
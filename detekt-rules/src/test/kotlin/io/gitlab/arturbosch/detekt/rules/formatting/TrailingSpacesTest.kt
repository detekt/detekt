package io.gitlab.arturbosch.detekt.rules.formatting

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.test.RuleTest
import io.gitlab.arturbosch.detekt.test.format
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

/**
 * @author Shyiko
 */
class TrailingSpacesTest : RuleTest {

	override val rule: Rule = TrailingSpaces(Config.empty)

	@Test
	fun testLint() {
		assertThat(rule.lint("fun main() {\n    val a = 1\n\n \n} "), hasSize(equalTo(2)))
	}

	@Test
	fun testFormat() {
		assertThat(rule.format("fun main() {\n    val a = 1 \n  \n \n} "),
				equalTo("fun main() {\n    val a = 1\n\n\n}"))
	}
}
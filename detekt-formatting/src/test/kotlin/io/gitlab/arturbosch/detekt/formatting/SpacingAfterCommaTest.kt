package io.gitlab.arturbosch.detekt.formatting

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.test.RuleTest
import io.gitlab.arturbosch.detekt.test.format
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Test

/**
 * @author Shyiko
 */
class SpacingAfterCommaTest : RuleTest {

	override val rule: Rule = SpacingAfterComma(Config.Companion.empty)

	@Test
	fun testLint() {
		assertThat(rule.lint("fun main() { x(1,3); x(1, 3); println(\",\") }"), hasSize(equalTo(1)))
		assertThat(rule.lint("enum class E { A, B,C }"), hasSize(equalTo(1)))
	}

	@Test
	fun testFormat() {
		assertThat(rule.format("fun main() { x(1,3); x(1, 3) }"),
				equalTo("fun main() { x(1, 3); x(1, 3) }"))
	}
}
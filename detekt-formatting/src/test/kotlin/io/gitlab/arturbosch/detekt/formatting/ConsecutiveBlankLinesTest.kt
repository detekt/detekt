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
class ConsecutiveBlankLinesTest : RuleTest {

	override val rule: Rule = ConsecutiveBlankLines(Config.Companion.empty)

	@Test
	fun threeNewLinesAreTooMuch() {
		assertThat(rule.lint("fun main() {\n\n\n}"), hasSize(equalTo(1)))
	}

	@Test
	fun testFormat() {
		assertThat(rule.format("fun main() {\n\n\n}\n\n"), equalTo("fun main() {\n\n}\n"))
	}
}
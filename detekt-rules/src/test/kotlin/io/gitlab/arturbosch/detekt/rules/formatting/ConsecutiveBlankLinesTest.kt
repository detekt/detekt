package io.gitlab.arturbosch.detekt.rules.formatting

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.format
import io.gitlab.arturbosch.detekt.rules.lint
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
class ConsecutiveBlankLinesTest : RuleTest {

	override val rule: Rule = ConsecutiveBlankLines(Config.empty)

	@Test
	fun threeNewLinesAreTooMuch() {
		assertThat(rule.lint("fun main() {\n\n\n}"), hasSize(equalTo(1)))
	}

	@Test
	fun testFormat() {
		assertThat(rule.format("fun main() {\n\n\n}\n\n"), equalTo("fun main() {\n\n}\n"))
	}
}
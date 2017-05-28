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
class ConsecutiveBlankLinesTest : RuleTest {

	override val rule: Rule = ConsecutiveBlankLines(Config.empty)

	@Test
	fun threeNewLinesAreTooMuch() {
		assertThat(rule.lint("fun main() {\n\n\n\tcall()\n\n\n}")).hasSize(2)
	}

	@Test
	fun newLinesTrimmedIndentationPreserved() {
		assertThat(rule.format("fun main() {\n\n\n\tcall()\n}\n\n\n")).isEqualTo("fun main() {\n\n\tcall()\n}\n\n")
	}

}
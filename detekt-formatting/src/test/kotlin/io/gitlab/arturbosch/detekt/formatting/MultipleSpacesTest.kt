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
class MultipleSpacesTest : RuleTest {

	override val rule: Rule = MultipleSpaces(Config.Companion.empty)

	@Test
	fun testLint() {
		assertThat(rule.lint("fun main() { x(1, 3);  x(1, 3)\n  \n  }"), hasSize(equalTo(1)))
	}

	@Test
	fun testFormat() {
		assertThat(rule.format("""fun main() { x(1, 3);  x(1, 3)\n  \n  }"""),
				equalTo("""fun main() { x(1, 3); x(1, 3)\n \n }"""))
	}

}
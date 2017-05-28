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
class MultipleSpacesTest : RuleTest {

	override val rule: Rule = MultipleSpaces(Config.empty)

	@Test
	fun testLint() {
		assertThat(rule.lint("""fun  main()  {  call(x, y);  call(x, y)\n  \n  }""")).hasSize(6)
	}

	@Test
	fun testFormat() {
		assertThat(rule.format("""fun  main()  {  call(x, y);  call(x, y)\n  \n  }"""))
				.isEqualTo("""fun main() { call(x, y); call(x, y)\n \n }""")
	}

}
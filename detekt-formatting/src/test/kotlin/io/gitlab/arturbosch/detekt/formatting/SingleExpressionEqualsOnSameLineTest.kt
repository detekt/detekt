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
internal class SingleExpressionEqualsOnSameLineTest : RuleTest {

	override val rule: Rule = SingleExpressionEqualsOnSameLine(Config.Companion.empty)

	@Test
	fun onlyExpressionSyntaxIsChecked() {
		assertThat(rule.lint("""
fun stuff() =
 	5
fun stuff2() {
	5
}
"""
		)).hasSize(1)
	}

	@Test
	fun formatEqualsOnSameLineCases() {
		assertThat(rule.format("""
fun stuff() =
 	5
"""
		)).isEqualTo("fun stuff() = 5")

		assertThat(rule.format("""
fun stuff()
	=
	5
"""
		)).isEqualTo("fun stuff()\n\t= 5")

		assertThat(rule.format("""
fun stuff() =

 	println()
"""
		)).isEqualTo("fun stuff() = println()")

		assertThat(rule.format("""
fun stuff() =

	// ups comment


 	5
"""
		)).isEqualTo("fun stuff() = // ups comment\n5")
	}

	@Test
	fun formatIfMethodChainOnSameLine() {
		assertThat(rule.format("""
fun stuff()
	=
 		future { 5 }.onSuccess { 6 }
"""
		)).isEqualTo("fun stuff()\n\t= future { 5 }.onSuccess { 6 }")
	}

	@Test
	fun doNotFormatIfMethodChainNotOnOneLine() {
		val content = """
fun stuff()
	=
 		future {
			5
		}
 		.onSuccess {
 			6
		}
""".trimIndent()
		assertThat(rule.format(content)).isEqualTo(content)

	}
}
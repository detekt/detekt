package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.RuleTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class OptionalAbstractKeywordTest : RuleTest {

	override val rule = OptionalAbstractKeyword()

	@Test
	fun noAbstractKeywordOnInterface() {
		val code = "interface A {}"
		Assertions.assertThat(lint(code)).isEqualTo(0)
	}

	@Test
	fun abstractInterfaceProperty() {
		val code = "abstract interface A { abstract var x: Int }"
		Assertions.assertThat(lint(code)).isEqualTo(2)
	}

	@Test
	fun abstractInterfaceFunction() {
		val code = "abstract interface A { abstract fun x() }"
		Assertions.assertThat(lint(code)).isEqualTo(2)
	}

	@Test
	fun loneAbstractMember() {
		val code = "abstract var x: Int"
		Assertions.assertThat(lint(code)).isEqualTo(0)
	}


	@Test
	fun innerInterface() {
		val code = "class A { abstract interface B {} }"
		Assertions.assertThat(lint(code)).isEqualTo(1)
	}

	@Test
	fun abstractClass() {
		val code = "abstract class A { abstract fun x() }"
		Assertions.assertThat(lint(code)).isEqualTo(0)
	}

	private fun lint(code: String) = rule.lint(code).size
}

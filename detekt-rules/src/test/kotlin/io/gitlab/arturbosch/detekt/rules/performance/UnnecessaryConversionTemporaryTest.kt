package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class UnnecessaryConversionTemporaryTest {

	@Test
	fun findWrongIntegerConversion() {
		val code = "val i = Integer(1).toString()"
		Assertions.assertThat(UnnecessaryConversionTemporary().lint(code)).hasSize(1)
	}

	@Test
	fun rightIntegerConversion() {
		val code = "val i = Integer.toString(1)"
		Assertions.assertThat(UnnecessaryConversionTemporary().lint(code)).hasSize(0)
	}
}

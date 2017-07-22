package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class SafeCastTest {

	@Test
	fun findNegated() {
		val code = "fun test() {\n" +
				"        if (element !is KtElement) {\n" +
				"            null\n" +
				"        } else {\n" +
				"            element\n" +
				"        }\n" +
				"    }"
		findOne(code)
	}

	@Test
	fun find() {
		val code = "fun test() {\n" +
				"        if (element is KtElement) {\n" +
				"            element\n" +
				"        } else {\n" +
				"            null\n" +
				"        }\n" +
				"    }"
		findOne(code)
	}

	private fun findOne(code: String) {
		val findings = SafeCast().lint(code)
		Assertions.assertThat(findings.size).isEqualTo(1)
	}
}

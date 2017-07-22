package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Finding
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

	@Test
	fun wrongCondition() {
		val code = "fun test() {\n" +
				"        if (element == other) {\n" +
				"            element\n" +
				"        } else {\n" +
				"            null\n" +
				"        }\n" +
				"    }"
		findNone(code)
	}

	@Test
	fun wrongElseClause() {
		val code = "fun test() {\n" +
				"        if (element is KtElement) {\n" +
				"            element\n" +
				"        } else {\n" +
				"            KtElement()\n" +
				"        }\n" +
				"    }"
		findNone(code)
	}

	private fun findOne(code: String) {
		val findings = retrieveFindings(code)
		Assertions.assertThat(findings.size).isEqualTo(1)
	}

	private fun findNone(code: String) {
		val findings = retrieveFindings(code)
		Assertions.assertThat(findings.size).isEqualTo(0)
	}

	private fun retrieveFindings(code: String): List<Finding> {
		return SafeCast().lint(code)
	}
}

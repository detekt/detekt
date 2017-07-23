package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.test.RuleTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class SafeCastTest : RuleTest {

	override val rule = SafeCast()

	@Test
	fun findNegated() {
		val code = """
				fun test() {
					if (element !is KtElement) {
						null
					} else {
						element
					}
				}
		"""
		findOne(code)
	}

	@Test
	fun find() {
		val code = """
				fun test() {
					if (element is KtElement) {
						element
					} else {
						null
					}
				}
		"""
		findOne(code)
	}

	@Test
	fun wrongCondition() {
		val code = """
				fun test() {
					if (element == other) {
						element
					} else {
						null
					}
				}
		"""
		findNone(code)
	}

	@Test
	fun wrongElseClause() {
		val code = """
				fun test() {
					if (element is KtElement) {
						element
					} else {
						KtElement()
					}
				}
		"""
		findNone(code)
	}

	private fun findOne(code: String) {
		val findings = rule.lint(code)
		Assertions.assertThat(findings.size).isEqualTo(1)
	}

	private fun findNone(code: String) {
		val findings = rule.lint(code)
		Assertions.assertThat(findings.size).isEqualTo(0)
	}
}

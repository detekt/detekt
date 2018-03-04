package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Java6Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.dsl.SubjectProviderDsl

class StringLiteralDuplicationSpec : SubjectSpek<StringLiteralDuplication>({

	subject { StringLiteralDuplication() }

	given("many hardcoded strings") {

		it("reports 3 equal hardcoded strings") {
			val code = """
				class Duplication {
					var s1 = "lorem"
					fun f(s: String = "lorem") {
						s1.equals("lorem")
					}
				}"""
			assertCodeFindings(code, 1)
		}

		it("does not report 2 equal hardcoded strings") {
			val code = """val str = "lorem" + "lorem" + "ipsum""""
			assertCodeFindings(code, 0)
		}
	}

	given("strings in annotations") {

		val code = """
		@Suppress("unused")
		class A
		@Suppress("unused")
		class B
		@Suppress("unused")
		class C
		""""

		it("does not report strings in annotations") {
			assertCodeFindings(code, 0)
		}

		it("reports strings in annotations according to config") {
			val config = TestConfig(mapOf(StringLiteralDuplication.IGNORE_ANNOTATION to "false"))
			assertFindingWithConfig(code, config, 1)
		}
	}

	given("strings with less than 5 characters") {

		val code = """val str = "amet" + "amet" + "amet""""

		it("does not report strings with 4 characters") {
			assertCodeFindings(code, 0)
		}

		it("reports string with 4 characters") {
			val config = TestConfig(mapOf(StringLiteralDuplication.EXCLUDE_SHORT_STRING to "false"))
			assertFindingWithConfig(code, config, 1)
		}
	}

	given("strings with values to match for the regex") {

		it("does not report lorem or ipsum according to config in regex") {
			val code = """
				val str1 = "lorem" + "lorem" + "lorem"
				val str2 = "ipsum" + "ipsum" + "ipsum"
			"""
			val config = TestConfig(mapOf(StringLiteralDuplication.IGNORE_STRINGS_REGEX to "(lorem|ipsum)"))
			assertFindingWithConfig(code, config, 0)
		}
	}

	describe("saves string literal references") {

		it("reports 3 locations for 'lorem'") {
			val code = """
				class Duplication {
					var s1 = "lorem"
					fun f(s: String = "lorem") {
						s1.equals("lorem")
					}
				}"""
			val finding = subject.lint(code)[0]
			val locations = finding.references.map { it.location } + finding.entity.location
			assertThat(locations).hasSize(3)
		}
	}
})

private fun SubjectProviderDsl<StringLiteralDuplication>.assertCodeFindings(code: String, expected: Int) {
	assertThat(subject.lint(code)).hasSize(expected)
}

private fun assertFindingWithConfig(code: String, config: TestConfig, expected: Int) {
	val findings = StringLiteralDuplication(config).lint(code)
	assertThat(findings).hasSize(expected)
}

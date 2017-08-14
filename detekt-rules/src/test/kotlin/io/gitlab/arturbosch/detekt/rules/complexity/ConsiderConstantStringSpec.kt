package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Java6Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.dsl.SubjectProviderDsl

class ConsiderConstantStringSpec : SubjectSpek<ConsiderConstantString>({
	subject { ConsiderConstantString() }

	given("many hardcoded strings") {

		it("reports 3 equal hardcoded strings") {
			val code = """
				class Duplication {
					var s1 = "Foo"
					fun f(s: String = "Foo") {
						s1.equals("Foo")
					}
				}"""
			assertCodeFindings(code, 1)
		}

		it("does not report 2 equal hardcoded strings") {
			val code = """val str = "Foo" + "Foo" + "Bar""""
			assertCodeFindings(code, 0)
		}
	}

	given("strings with values to match for the regex") {

		it("reports comma in regex") {
			val code = """
				val str1 = "," + "," + ","
				val str2 = ", " + ", " + ", "
			"""
			assertCodeFindings(code, 2)
		}

		it("reports dot in regex") {
			val code = """
				val str1 = "." + "." + "."
				val str2 = ". " + ". " + ". "
			"""
			assertCodeFindings(code, 2)
		}

		it("does not report multiple strings containing a dot") {
			val code = """val str = "a." + "b." + "c.""""
			assertCodeFindings(code, 0)
		}

		it("does report FOO according to config in regex") {
			val code = """val str = "FOO" + "FOO" + "FOO""""
			val config = TestConfig(mapOf(ConsiderConstantString.IGNORE_STRINGS_REGEX to """^("FOO")$"""))
			assertOneCodeFindingWithConfig(code, config)
		}
	}

	given("strings with whitespaces") {

		val code = """val str = " " + " " + " """"

		it("does not report whitespaces in strings") {
			assertCodeFindings(code, 0)
		}

		it("reports whitespaces according to config") {
			val config = TestConfig(mapOf(ConsiderConstantString.IGNORE_WHITESPACES to "false"))
			assertOneCodeFindingWithConfig(code, config)
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
			val config = TestConfig(mapOf(ConsiderConstantString.IGNORE_ANNOTATION to "false"))
			assertOneCodeFindingWithConfig(code, config)
		}
	}
})

private fun SubjectProviderDsl<ConsiderConstantString>.assertCodeFindings(code: String, expected: Int) {
	assertThat(subject.lint(code)).hasSize(expected)
}

private fun assertOneCodeFindingWithConfig(code: String, config: TestConfig) {
	val findings = ConsiderConstantString(config).lint(code)
	assertThat(findings).hasSize(1)
}

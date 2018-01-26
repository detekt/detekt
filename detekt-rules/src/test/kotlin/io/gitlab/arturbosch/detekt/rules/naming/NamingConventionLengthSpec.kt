package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class NamingConventionLengthSpec : SubjectSpek<NamingRules>({

	subject { NamingRules() }

	it("should not report underscore variable names") {
		val code = "val (_, status) = getResult()"
		subject.lint(code)
		assertThat(subject.findings).isEmpty()
	}

	it("should not report a variable with single letter name") {
		val code = "private val a = 3"
		subject.lint(code)
		assertThat(subject.findings).hasSize(0)
	}

	describe("VariableMinLength rule with a custom minimum length") {

		val variableMinLength = VariableMinLength(TestConfig(mapOf(VariableMinLength.MINIMUM_VARIABLE_NAME_LENGTH to "2")))

		it("reports a very short variable name") {
			val code = "private val a = 3"
			assertThat(variableMinLength.lint(code)).hasSize(1)
		}

		it("does not report a variable with only a single underscore") {
			val code = """
			class C {
				val prop: (Int) -> Unit = { _ -> Unit }
			}"""
			assertThat(variableMinLength.lint(code)).hasSize(0)
		}
	}

	it("should not report a variable with 64 letters") {
		val code = "private val varThatIsExactly64LettersLongWhichYouMightNotWantToBelieveInLolz = 3"
		subject.lint(code)
		assertThat(subject.findings).hasSize(0)
	}

	it("should report a variable name that is too long") {
		val code = "private val thisVariableIsDefinitelyWayTooLongLongerThanEverythingAndShouldBeMuchShorter = 3"
		subject.lint(code)
		assertThat(subject.findings).hasSize(1)
	}

	it("should not report a variable name that is okay") {
		val code = "private val thisOneIsCool = 3"
		subject.lint(code)
		assertThat(subject.findings).isEmpty()
	}

	it("should report a function name that is too short") {
		val code = "private fun a = 3"
		subject.lint(code)
		assertThat(subject.findings).hasSize(1)
	}

	it("should report a function name that is too long") {
		val code = "private fun thisFunctionIsDefinitelyWayTooLongAndShouldBeMuchShorter = 3"
		subject.lint(code)
		assertThat(subject.findings).hasSize(1)
	}

	it("should not report a function name that is okay") {
		val code = "private fun three = 3"
		subject.lint(code)
		assertThat(subject.findings).isEmpty()
	}

	it("should not report a function name that begins with a backtick, capitals, and spaces") {
		val code = "private fun `Hi bye` = 3"
		subject.lint(code)
		assertThat(subject.findings).isEmpty()
	}
})

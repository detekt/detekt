package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class NamingConventionSpec : Spek({

	given("a kt file with crazy naming schemes") {
		val code = """
			package test

			class __TEST__ {
				val TEST = 1

				fun UpperCaseMethod() {
				}

				companion object {
					const val constant: String = "test"
				}
			}

			enum class __ENUM__ {
				enumvalue
			}
		"""

		val file = compileContentForTest(code).text

		it("should ignore all naming violations if rule is turned off") {
			val rule = NamingConventionViolation(TestConfig(mapOf("active" to "false")))

			val findings = rule.lint(file)
			Assertions.assertThat(findings).isEmpty()
		}

		it("should report all naming violations") {
			val rule = NamingConventionViolation()

			val findings = rule.lint(file)
			Assertions.assertThat(findings).hasSize(6)
		}

		it("should report no violations with custom naming scheme") {
			val rule = NamingConventionViolation(TestConfig(mapOf(
					"variablePattern" to "TEST",
					"constantPattern" to "constant",
					"methodPattern" to "UpperCaseMethod",
					"classPattern" to "__([A-Z]+)__",
					"enumEntryPattern" to "enumvalue"
			)))

			val findings = rule.lint(file)
			Assertions.assertThat(findings).isEmpty()
		}
	}
})

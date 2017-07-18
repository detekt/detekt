package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class WildcardImportSpec : Spek({

	given("a kt file with wildcard imports") {
		val code = """
			package test

			import io.gitlab.arturbosch.detekt.*
			import test.test.detekt.*

			class Test {
			}
		"""

		val file = compileContentForTest(code).text

		it("should not report anything when the rule is turned off") {
			val rule = WildcardImport(TestConfig(mapOf("active" to "false")))

			val findings = rule.lint(file)
			Assertions.assertThat(findings).isEmpty()
		}

		it("should report all wildcard imports") {
			val rule = WildcardImport()

			val findings = rule.lint(file)
			Assertions.assertThat(findings).hasSize(2)
		}

		it("should not report excluded wildcard imports") {
			val rule = WildcardImport(TestConfig(mapOf("excludedImports" to "test.test.*")))

			val findings = rule.lint(file)
			Assertions.assertThat(findings).hasSize(1)
		}
	}

	given("a kt file with no wildcard imports") {
		val code = """
			package test

			import test.Test

			class Test {
			}
		"""

		it("should not report any issues") {
			val findings = WildcardImport().lint(code)
			Assertions.assertThat(findings).isEmpty()
		}
	}
})

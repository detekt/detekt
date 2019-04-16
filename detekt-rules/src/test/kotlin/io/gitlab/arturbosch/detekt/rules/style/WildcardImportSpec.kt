package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class WildcardImportSpec : Spek({

    describe("WildcardImport rule") {

        context("a kt file with wildcard imports") {
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
                assertThat(findings).isEmpty()
            }

            it("should report all wildcard imports") {
                val rule = WildcardImport()

                val findings = rule.lint(file)
                assertThat(findings).hasSize(2)
            }

            it("should not report excluded wildcard imports") {
                val rule = WildcardImport(TestConfig(mapOf("excludeImports" to "test.test.*")))

                val findings = rule.lint(file)
                assertThat(findings).hasSize(1)
            }

            it("should not report excluded wildcard imports when multiple are excluded") {
                val rule = WildcardImport(TestConfig(mapOf("excludeImports" to "test.test.*, io.gitlab.arturbosch.detekt")))

                val findings = rule.lint(file)
                assertThat(findings).isEmpty()
            }

            it("ignores excludes that are not matching") {
                val rule = WildcardImport(TestConfig(mapOf("excludeImports" to "other.test.*")))

                val findings = rule.lint(file)
                assertThat(findings).hasSize(2)
            }
        }

        context("a kt file with no wildcard imports") {
            val code = """
			package test

			import test.Test

			class Test {
			}
		"""

            it("should not report any issues") {
                val findings = WildcardImport().lint(code)
                assertThat(findings).isEmpty()
            }
        }
    }
})

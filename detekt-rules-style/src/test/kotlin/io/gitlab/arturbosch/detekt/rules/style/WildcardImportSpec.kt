package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.style.WildcardImport.Companion.EXCLUDED_IMPORTS
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class WildcardImportSpec : Spek({

    describe("WildcardImport rule") {

        context("a kt file with wildcard imports") {
            val code = """
                import io.gitlab.arturbosch.detekt.*
                import org.spekframework.*

                class Test {
                }
            """

            it("should not report anything when the rule is turned off") {
                val rule = WildcardImport(TestConfig(mapOf(Config.ACTIVE_KEY to "false")))

                val findings = rule.compileAndLint(code)
                assertThat(findings).isEmpty()
            }

            it("should report all wildcard imports") {
                val rule = WildcardImport()

                val findings = rule.compileAndLint(code)
                assertThat(findings).hasSize(2)
            }

            it("should not report excluded wildcard imports") {
                val rule = WildcardImport(TestConfig(mapOf(EXCLUDED_IMPORTS to listOf("org.spekframework.*"))))

                val findings = rule.compileAndLint(code)
                assertThat(findings).hasSize(1)
            }

            it("should not report excluded wildcard imports when multiple are excluded") {
                val rule = WildcardImport(TestConfig(mapOf(EXCLUDED_IMPORTS to listOf("org.spekframework.*", "io.gitlab.arturbosch.detekt"))))

                val findings = rule.compileAndLint(code)
                assertThat(findings).isEmpty()
            }

            it("should not report excluded wildcard imports when multiple are excluded using config string") {
                val rule = WildcardImport(TestConfig(mapOf(EXCLUDED_IMPORTS to "org.spekframework.*, io.gitlab.arturbosch.detekt")))

                val findings = rule.compileAndLint(code)
                assertThat(findings).isEmpty()
            }

            it("ignores excludes that are not matching") {
                val rule = WildcardImport(TestConfig(mapOf(EXCLUDED_IMPORTS to listOf("other.test.*"))))

                val findings = rule.compileAndLint(code)
                assertThat(findings).hasSize(2)
            }

            it("ignores the default values") {
                val code2 = """
                    import java.util.*
                    import kotlinx.android.synthetic.*
                """

                val findings = WildcardImport().lint(code2)
                assertThat(findings).isEmpty()
            }
        }

        context("a kt file with no wildcard imports") {
            val code = """
            package org

            import org.spekframework.spek2.Spek

            class Test {
            }
        """

            it("should not report any issues") {
                val findings = WildcardImport().compileAndLint(code)
                assertThat(findings).isEmpty()
            }
        }
    }
})

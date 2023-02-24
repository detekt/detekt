package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val EXCLUDED_IMPORTS = "excludeImports"

class WildcardImportSpec {

    @Nested
    inner class `a kt file with wildcard imports` {
        val code = """
            import io.gitlab.arturbosch.detekt.*
            import io.mockk.*
            
            class Test {
            }
        """.trimIndent()

        @Test
        fun `should not report anything when the rule is turned off`() {
            val rule = WildcardImport(TestConfig(Config.ACTIVE_KEY to "false"))

            val findings = rule.compileAndLint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report all wildcard imports`() {
            val rule = WildcardImport()

            val findings = rule.compileAndLint(code)
            assertThat(findings).hasSize(2)
        }

        @Test
        fun `should not report excluded wildcard imports`() {
            val rule = WildcardImport(TestConfig(EXCLUDED_IMPORTS to listOf("io.mockk.*")))

            val findings = rule.compileAndLint(code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not report excluded wildcard imports when multiple are excluded`() {
            val rule = WildcardImport(
                TestConfig(
                    EXCLUDED_IMPORTS to listOf(
                        "io.mockk.*",
                        "io.gitlab.arturbosch.detekt"
                    )
                )
            )

            val findings = rule.compileAndLint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report excluded wildcard imports when multiple are excluded using config string`() {
            val rule =
                WildcardImport(TestConfig(EXCLUDED_IMPORTS to "io.mockk.*, io.gitlab.arturbosch.detekt"))

            val findings = rule.compileAndLint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `ignores excludes that are not matching`() {
            val rule = WildcardImport(TestConfig(EXCLUDED_IMPORTS to listOf("other.test.*")))

            val findings = rule.compileAndLint(code)
            assertThat(findings).hasSize(2)
        }

        @Test
        fun `ignores the default values`() {
            val code2 = """
                import java.util.*
            """.trimIndent()

            val findings = WildcardImport().lint(code2)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a kt file with no wildcard imports` {
        val code = """
            package org
            
            import io.mockk.mockk
            
            class Test {
            }
        """.trimIndent()

        @Test
        fun `should not report any issues`() {
            val findings = WildcardImport().compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }
}

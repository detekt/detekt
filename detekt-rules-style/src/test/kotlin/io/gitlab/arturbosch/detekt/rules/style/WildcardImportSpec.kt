package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val EXCLUDED_IMPORTS = "excludeImports"

class WildcardImportSpec {

    @Nested
    inner class `a kt file with wildcard imports` {
        val code = """
            import java.io.*
            import java.time.*
            
            class Test {
            }
        """.trimIndent()

        @Test
        fun `should report all wildcard imports`() {
            val rule = WildcardImport(Config.empty)

            val findings = rule.lint(code)
            assertThat(findings).hasSize(2)
        }

        @Test
        fun `should not report excluded wildcard imports`() {
            val rule = WildcardImport(TestConfig(EXCLUDED_IMPORTS to listOf("java.io.*")))

            val findings = rule.lint(code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not report excluded wildcard imports when multiple are excluded`() {
            val rule = WildcardImport(
                TestConfig(
                    EXCLUDED_IMPORTS to listOf(
                        "java.io.*",
                        "java.time"
                    )
                )
            )

            val findings = rule.lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `ignores excludes that are not matching`() {
            val rule = WildcardImport(TestConfig(EXCLUDED_IMPORTS to listOf("other.test.*")))

            val findings = rule.lint(code)
            assertThat(findings).hasSize(2)
        }

        @Test
        fun `ignores the default values`() {
            val code2 = """
                import java.util.*
            """.trimIndent()

            val findings = WildcardImport(Config.empty).lint(code2)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a kt file with no wildcard imports` {
        val code = """
            package org
            
            import java.io.File
            
            class Test {
            }
        """.trimIndent()

        @Test
        fun `should not report any issues`() {
            val findings = WildcardImport(Config.empty).lint(code)
            assertThat(findings).isEmpty()
        }
    }
}

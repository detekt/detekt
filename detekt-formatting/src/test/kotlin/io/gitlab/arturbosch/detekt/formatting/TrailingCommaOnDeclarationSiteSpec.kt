package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.formatting.wrappers.TrailingCommaOnDeclarationSite
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Some test cases were used directly from KtLint to verify the wrapper rule:
 *
 * https://github.com/pinterest/ktlint/blob/master/ktlint-ruleset-standard/src/test/kotlin/com/pinterest/ktlint/ruleset/standard/TrailingCommaOnDeclarationSiteRuleTest.kt
 */
class TrailingCommaOnDeclarationSiteSpec {

    @Nested
    inner class UnnecessaryComma {

        @Test
        fun `reports unnecessary comma on generic type definition`() {
            val code = """
                class Foo1<A, B,> {}
            """.trimIndent()
            val findings = TrailingCommaOnDeclarationSite(TestConfig(mapOf(USE_TRAILING_COMMA to false))).lint(code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports unnecessary comma on property definition`() {
            val code = """
                data class Foo1(val bar: Int,)
                data class Foo2(
                   val bar: Int,
                )
            """.trimIndent()
            val findings = TrailingCommaOnDeclarationSite(TestConfig(mapOf(USE_TRAILING_COMMA to false))).lint(code)
            assertThat(findings).hasSize(2)
        }
    }

    @Nested
    inner class MissingComma {

        @Test
        fun `reports missing comma on generic type definition`() {
            val code = """
                class Foo1<
                    A,
                    B
                > {}
            """.trimIndent()
            val findings = TrailingCommaOnDeclarationSite(TestConfig(mapOf(USE_TRAILING_COMMA to true))).lint(code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports missing comma on property definition`() {
            val code = """
                data class Foo1(val bar: Int)
                data class Foo2(
                   val bar: Int
                )
            """.trimIndent()
            val findings = TrailingCommaOnDeclarationSite(TestConfig(mapOf(USE_TRAILING_COMMA to true))).lint(code)
            assertThat(findings).hasSize(1)
        }
    }

    private companion object {
        private const val USE_TRAILING_COMMA = "useTrailingCommaOnDeclarationSite"
    }
}

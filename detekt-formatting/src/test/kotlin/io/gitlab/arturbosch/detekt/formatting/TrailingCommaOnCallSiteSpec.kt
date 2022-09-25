package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.formatting.wrappers.TrailingCommaOnCallSite
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Some test cases were used directly from KtLint to verify the wrapper rule:
 *
 * https://github.com/pinterest/ktlint/blob/master/ktlint-ruleset-standard/src/test/kotlin/com/pinterest/ktlint/ruleset/standard/TrailingCommaOnCallSiteRuleTest.kt
 */
class TrailingCommaOnCallSiteSpec {

    @Nested
    inner class Unnecessary {

        @Test
        fun `reports unnecessary comma on function call`() {
            val code = """
                val foo1 = listOf("a", "b",)
            """.trimIndent()
            val findings = TrailingCommaOnCallSite(TestConfig(mapOf(USE_TRAILING_COMMA to false))).lint(code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports unnecessary comma on constructor call`() {
            val code = """
                val foo2 = Pair(1, 2,)
            """.trimIndent()
            val findings = TrailingCommaOnCallSite(TestConfig(mapOf(USE_TRAILING_COMMA to false))).lint(code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports unnecessary comma on generic type definition`() {
            val code = """
                val foo3: List<String,> = emptyList()
            """.trimIndent()
            val findings = TrailingCommaOnCallSite(TestConfig(mapOf(USE_TRAILING_COMMA to false))).lint(code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports unnecessary comma on array get`() {
            val code = """
                val foo4 = Array(2) { 42 }
                val bar4 = foo4[1,]
            """.trimIndent()
            val findings = TrailingCommaOnCallSite(TestConfig(mapOf(USE_TRAILING_COMMA to false))).lint(code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports unnecessary comma on annotation`() {
            val code = """
                @Foo5([1, 2,])
                val foo5: Int = 0
            """.trimIndent()
            val findings = TrailingCommaOnCallSite(TestConfig(mapOf(USE_TRAILING_COMMA to false))).lint(code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class MissingComma {

        @Test
        fun `reports missing comma on function call`() {
            val code = """
                val foo1 = listOf("a", "b")
                val foo2 = listOf(
                    "a", 
                    "b"
                )
            """.trimIndent()
            val findings = TrailingCommaOnCallSite(TestConfig(mapOf(USE_TRAILING_COMMA to true))).lint(code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports missing comma on constructor call`() {
            val code = """
                val foo2 = Pair(1, 2)
                val foo2 = Pair(
                    1, 
                    2
                )
            """.trimIndent()
            val findings = TrailingCommaOnCallSite(TestConfig(mapOf(USE_TRAILING_COMMA to true))).lint(code)
            assertThat(findings).hasSize(1)
        }
    }

    private companion object {
        private const val USE_TRAILING_COMMA = "useTrailingCommaOnCallSite"
    }
}

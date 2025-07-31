package io.gitlab.arturbosch.detekt.rules.naming

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val EXCLUDE_CLASS_PATTERN = "excludeClassPattern"

class FunctionParameterNamingSpec {

    @Nested
    inner class `parameters in a function of a class` {

        @Test
        fun `should detect no violations`() {
            val code = """
                class C {
                    fun someStuff(param: String) {}
                }
            """.trimIndent()
            assertThat(FunctionParameterNaming(Config.empty).lint(code)).isEmpty()
        }

        @Test
        fun `should not detect violations in overridden function`() {
            val code = """
                class C : I {
                    override fun someStuff(`object`: String) {}
                }
                interface I { fun someStuff(@Suppress("FunctionParameterNaming") `object`: String) }
            """.trimIndent()
            assertThat(FunctionParameterNaming(Config.empty).lint(code)).isEmpty()
        }

        @Test
        fun `should find some violations`() {
            val code = """
                class C {
                    fun someStuff(PARAM: String) {}
                }
            """.trimIndent()
            assertThat(FunctionParameterNaming(Config.empty).lint(code)).hasSize(1)
        }
    }

    @Nested
    inner class `parameters in a function of an excluded class` {

        val config = TestConfig(EXCLUDE_CLASS_PATTERN to "Excluded")

        @Test
        fun `should not detect function parameter`() {
            val code = """
                class Excluded {
                    fun f(PARAM: Int) {}
                }
            """.trimIndent()
            assertThat(FunctionParameterNaming(config).lint(code)).isEmpty()
        }

        @Test
        fun `should not detect constructor parameter`() {
            val code = "class Excluded(val PARAM: Int) {}"
            assertThat(FunctionParameterNaming(config).lint(code)).isEmpty()
        }
    }
}

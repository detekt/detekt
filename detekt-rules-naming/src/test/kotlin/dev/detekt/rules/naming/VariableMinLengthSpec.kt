package dev.detekt.rules.naming

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class VariableMinLengthSpec {

    @Nested
    inner class `VariableMinLength rule with a custom minimum length` {

        private val variableMinLength =
            VariableMinLength(TestConfig(VariableMinLength.MINIMUM_VARIABLE_NAME_LENGTH to 2))

        @Test
        fun `reports a very short variable name`() {
            val code = "private val a = 3"
            assertThat(variableMinLength.lint(code)).hasSize(1)
        }

        @Test
        fun `does not report a variable with only a single underscore`() {
            val code = """
                class C {
                    val prop: (Int) -> Unit = { _ -> Unit }
                }
            """.trimIndent()
            assertThat(variableMinLength.lint(code)).isEmpty()
        }
    }

    @Test
    fun `should not report a variable name that is okay`() {
        val code = "private val thisOneIsCool = 3"
        assertThat(VariableMinLength(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `should not report a variable with single letter name`() {
        val code = "private val a = 3"
        assertThat(VariableMinLength(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `should not report underscore variable names`() {
        val code = """
            fun getResult(): Pair<String, String> = TODO()
            fun function() {
                val (_, status) = getResult()
            }
        """.trimIndent()
        assertThat(VariableMinLength(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `should not report an overridden variable name that is too short`() {
        val code = """
            class C : I {
                override val shortButOk = "banana"
            }
            interface I : I2 {
                override val shortButOk: String
            }
            interface I2 {
                @Suppress("VariableMinLength") val shortButOk: String
            }
        """.trimIndent()
        assertThat(
            VariableMinLength(TestConfig("minimumVariableNameLength" to 15)).lint(code)
        ).isEmpty()
    }
}

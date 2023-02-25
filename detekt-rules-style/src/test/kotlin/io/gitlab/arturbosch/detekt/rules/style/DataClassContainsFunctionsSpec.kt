package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val CONVERSION_FUNCTION_PREFIX = "conversionFunctionPrefix"
private const val ALLOW_OPERATORS = "allowOperators"

class DataClassContainsFunctionsSpec {
    val subject = DataClassContainsFunctions()

    @Nested
    inner class `flagged functions in data class` {
        val code = """
            data class C(val s: String) {
                fun f() {}
            
                data class Nested(val i: Int) {
                    fun toConversion() = C(i.toString())
                }
            }
        """.trimIndent()

        @Test
        fun `reports valid data class with conversion function`() {
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports valid data class without conversion function string`() {
            val config = TestConfig(CONVERSION_FUNCTION_PREFIX to "")
            val rule = DataClassContainsFunctions(config)
            assertThat(rule.compileAndLint(code)).hasSize(2)
        }

        @Test
        fun `reports valid data class without conversion function list`() {
            val config = TestConfig(CONVERSION_FUNCTION_PREFIX to emptyList<String>())
            val rule = DataClassContainsFunctions(config)
            assertThat(rule.compileAndLint(code)).hasSize(2)
        }
    }

    @Nested
    inner class `operators in data class` {
        val code = """
            data class Vector2(val x: Float, val y: Float) {
                operator fun plus(other: Vector2): Vector2 = Vector2(x + other.x, y + other.y)
            }
        """.trimIndent()

        @Test
        fun `reports operators if not allowed by default`() {
            val rule = DataClassContainsFunctions()
            assertThat(rule.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports operators if not allowed`() {
            val config = TestConfig(ALLOW_OPERATORS to false)
            val rule = DataClassContainsFunctions(config)
            assertThat(rule.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `does not report operators if allowed`() {
            val config = TestConfig(ALLOW_OPERATORS to true)
            val rule = DataClassContainsFunctions(config)
            assertThat(rule.compileAndLint(code)).isEmpty()
        }
    }

    @Test
    fun `does not report a data class without a function`() {
        val code = "data class C(val i: Int)"
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report a non-data class without a function`() {
        val code = """
            class C {
                fun f() {}
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report a data class with overridden functions`() {
        val code = """
            data class C(val i: Int) {
            
                override fun hashCode(): Int {
                    return super.hashCode()
                }
            
                override fun equals(other: Any?): Boolean {
                    return super.equals(other)
                }
            
                override fun toString(): String {
                    return super.toString()
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }
}

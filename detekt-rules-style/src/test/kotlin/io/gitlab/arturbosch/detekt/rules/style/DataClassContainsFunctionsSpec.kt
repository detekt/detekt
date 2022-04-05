package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val CONVERSION_FUNCTION_PREFIX = "conversionFunctionPrefix"

class DataClassContainsFunctionsSpec {
    val subject = DataClassContainsFunctions()

    @Nested
    inner class `DataClassContainsFunctions rule` {

        @Nested
        inner class `flagged functions in data class` {
            val code = """
                data class C(val s: String) {
                    fun f() {}

                    data class Nested(val i: Int) {
                        fun toConversion() = C(i.toString())
                    }
                }
            """

            @Test
            fun `reports valid data class with conversion function`() {
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            @Test
            fun `reports valid data class without conversion function`() {
                val config = TestConfig(mapOf(CONVERSION_FUNCTION_PREFIX to ""))
                val rule = DataClassContainsFunctions(config)
                assertThat(rule.compileAndLint(code)).hasSize(2)
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
            """
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
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
}

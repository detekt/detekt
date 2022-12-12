package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class FunctionMaxLengthSpec {

    @Test
    fun `should report a function name that is too long base on config`() {
        val code = "fun thisFunctionLongName() = 3"
        assertThat(
            FunctionMaxLength(TestConfig(mapOf("maximumFunctionNameLength" to 10)))
                .compileAndLint(code)
        )
            .hasSize(1)
    }

    @Test
    fun `should not report an overridden function name that is too long`() {
        val code = """
        class C : I {
            override fun thisFunctionIsWayTooLongButStillShouldNotBeReportedByDefault() {}
        }
        interface I { @Suppress("FunctionMaxLength") fun thisFunctionIsWayTooLongButStillShouldNotBeReportedByDefault() }
        """.trimIndent()
        assertThat(
            FunctionMaxLength(
                TestConfig(mapOf("maximumFunctionNameLength" to 10))
            ).compileAndLint(code)
        ).isEmpty()
    }

    @Test
    fun `should report a function name that is too long`() {
        val code = "fun thisFunctionIsDefinitelyWayTooLongAndShouldBeMuchShorter() = 3"
        assertThat(FunctionMaxLength().compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `should not report a function name that is okay`() {
        val code = "fun three() = 3"
        assertThat(FunctionMaxLength().compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should not report an operator function`() {
        val code = """
            data class Point2D(var x: Int = 0, var y: Int = 0) {
                operator fun plusAssign(another: Point2D) {
                    x += another.x
                    y += another.y
                }
            }
        """.trimIndent()
        assertThat(
            FunctionMaxLength(
                TestConfig(
                    mapOf("maximumFunctionNameLength" to 5)
                )
            ).compileAndLint(code)
        ).isEmpty()
    }
}

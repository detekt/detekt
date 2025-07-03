package io.gitlab.arturbosch.detekt.rules.naming

import io.github.detekt.test.utils.KotlinEnvironmentContainer
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class FunctionNameMaxLengthSpec(val env: KotlinEnvironmentContainer) {

    @Test
    fun `should report a function name that is too long base on config`() {
        val code = "fun thisFunctionLongName() = 3"
        assertThat(
            FunctionNameMaxLength(TestConfig("maximumFunctionNameLength" to 10)).lint(code)
        ).hasSize(1)
    }

    @Test
    fun `should not report an overridden function name that is too long`() {
        val code = """
            class C : I {
                override fun thisFunctionIsWayTooLongButStillShouldNotBeReportedByDefault() {}
            }
            interface I { @Suppress("FunctionNameMaxLength") fun thisFunctionIsWayTooLongButStillShouldNotBeReportedByDefault() }
        """.trimIndent()
        assertThat(
            FunctionNameMaxLength(TestConfig("maximumFunctionNameLength" to 10)).lint(code)
        ).isEmpty()
    }

    @Test
    fun `should report a function name that is too long`() {
        val code = "fun thisFunctionIsDefinitelyWayTooLongAndShouldBeMuchShorter() = 3"
        assertThat(FunctionNameMaxLength(Config.empty).lint(code)).hasSize(1)
    }

    @Test
    fun `should not report a function name that is okay`() {
        val code = "fun three() = 3"
        assertThat(FunctionNameMaxLength(Config.empty).lint(code)).isEmpty()
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
            FunctionNameMaxLength(TestConfig("maximumFunctionNameLength" to 5)).lint(code)
        ).isEmpty()
    }
}

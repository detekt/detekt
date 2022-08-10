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
    fun `should report a function name that is too long`() {
        val code = "fun thisFunctionIsDefinitelyWayTooLongAndShouldBeMuchShorter() = 3"
        assertThat(FunctionMaxLength().compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `should not report a function name that is okay`() {
        val code = "fun three() = 3"
        assertThat(FunctionMaxLength().compileAndLint(code)).isEmpty()
    }
}

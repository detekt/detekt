package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FunctionMinLengthSpec {

    @Test
    fun `should report a function name that is too short`() {
        val code = "fun a() = 3"
        assertThat(FunctionMinLength().compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `should report a function name that is too short base on config`() {
        val code = "fun four() = 3"
        assertThat(
            FunctionMinLength(TestConfig(mapOf("minimumFunctionNameLength" to 5)))
                .compileAndLint(code)
        ).hasSize(1)
    }

    @Test
    fun `should not report a function name that is okay`() {
        val code = "fun three() = 3"
        assertThat(FunctionMinLength().compileAndLint(code)).isEmpty()
    }
}

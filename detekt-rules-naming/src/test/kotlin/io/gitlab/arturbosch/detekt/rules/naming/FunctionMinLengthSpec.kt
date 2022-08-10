package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class FunctionMinLengthSpec {

    @Test
    fun `should report a function name that is too short`() {
        val subject = FunctionMinLength()
        val code = "fun a() = 3"
        subject.compileAndLint(code)
        Assertions.assertThat(subject.findings).hasSize(1)
    }

    @Test
    fun `should report a function name that is too short base on config`() {
        val subject = FunctionMinLength(TestConfig(mapOf("minimumFunctionNameLength" to 5)))
        val code = "fun four() = 3"
        subject.compileAndLint(code)
        Assertions.assertThat(subject.findings).hasSize(1)
    }

    @Test
    fun `should not report a function name that is okay`() {
        val subject = FunctionMinLength()
        val code = "fun three() = 3"
        subject.compileAndLint(code)
        Assertions.assertThat(subject.findings).isEmpty()
    }

}

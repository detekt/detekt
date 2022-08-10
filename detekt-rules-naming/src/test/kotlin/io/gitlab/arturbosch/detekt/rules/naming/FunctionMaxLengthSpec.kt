package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class FunctionMaxLengthSpec {

    @Test
    fun `should report a function name that is too long base on config`() {
        val subject = FunctionMaxLength(TestConfig(mapOf("maximumFunctionNameLength" to 10)))
        val code = "fun thisFunctionLongName() = 3"
        subject.compileAndLint(code)
        Assertions.assertThat(subject.findings).hasSize(1)
    }

    @Test
    fun `should report a function name that is too long`() {
        val subject = FunctionMaxLength()
        val code = "fun thisFunctionIsDefinitelyWayTooLongAndShouldBeMuchShorter() = 3"
        subject.compileAndLint(code)
        Assertions.assertThat(subject.findings).hasSize(1)
    }

    @Test
    fun `should not report a function name that is okay`() {
        val subject = FunctionMaxLength()
        val code = "fun three() = 3"
        subject.compileAndLint(code)
        Assertions.assertThat(subject.findings).isEmpty()
    }

}

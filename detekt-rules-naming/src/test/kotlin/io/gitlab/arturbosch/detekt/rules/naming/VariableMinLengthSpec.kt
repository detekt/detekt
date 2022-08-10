package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class VariableMinLengthSpec {

    @Nested
    inner class `VariableMinLength rule with a custom minimum length` {

        val variableMinLength =
            VariableMinLength(TestConfig(mapOf(VariableMinLength.MINIMUM_VARIABLE_NAME_LENGTH to "2")))

        @Test
        fun `reports a very short variable name`() {
            val code = "private val a = 3"
            Assertions.assertThat(variableMinLength.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `does not report a variable with only a single underscore`() {
            val code = """
                class C {
                    val prop: (Int) -> Unit = { _ -> Unit }
            }
            """
            Assertions.assertThat(variableMinLength.compileAndLint(code)).isEmpty()
        }
    }

    @Test
    fun `should not report a variable name that is okay`() {
        val subject = VariableMinLength()
        val code = "private val thisOneIsCool = 3"
        subject.compileAndLint(code)
        Assertions.assertThat(subject.findings).isEmpty()
    }

    @Test
    fun `should not report a variable with single letter name`() {
        val subject = VariableMinLength()
        val code = "private val a = 3"
        subject.compileAndLint(code)
        Assertions.assertThat(subject.findings).isEmpty()
    }

    @Test
    fun `should not report underscore variable names`() {
        val subject = VariableMinLength()
        val code = """
            fun getResult(): Pair<String, String> = TODO()
            fun function() {
                val (_, status) = getResult()
            }
        """
        subject.compileAndLint(code)
        Assertions.assertThat(subject.findings).isEmpty()
    }
}

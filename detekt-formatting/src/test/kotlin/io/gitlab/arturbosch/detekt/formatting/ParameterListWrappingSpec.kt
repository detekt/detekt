package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.ParameterListWrapping
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ParameterListWrappingSpec {

    private lateinit var subject: ParameterListWrapping

    @BeforeEach
    fun createSubject() {
        subject = ParameterListWrapping(Config.empty)
    }

    @Nested
    inner class `ParameterListWrapping rule` {

        @Nested
        inner class `indent size equals 1` {

            val code = """
                fun f(
                 a: Int
                ) {}
            """.trimIndent()

            @Test
            fun `reports wrong indent size`() {
                assertThat(subject.lint(code)).hasSize(1)
            }

            @Test
            fun `does not report when using an indentation level config of 1`() {
                val config = TestConfig("indentSize" to "1")
                assertThat(ParameterListWrapping(config).lint(code)).isEmpty()
            }
        }

        @Test
        fun `does not report correct ParameterListWrapping level`() {
            val code = """
                fun f(
                    a: Int
                ) {}
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports when max line length is exceeded`() {
            val code = """
                fun f(a: Int, b: Int, c: Int) {}
            """.trimIndent()
            val config = TestConfig("maxLineLength" to "10")
            assertThat(ParameterListWrapping(config).lint(code)).hasSize(4)
        }
    }
}

package io.gitlab.arturbosch.detekt.formatting

import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.ArgumentListWrapping
import dev.detekt.test.TestConfig
import dev.detekt.test.assertThat
import org.junit.jupiter.api.Test

class ArgumentListWrappingSpec {

    @Test
    fun `reports wrong argument wrapping`() {
        val code = """
            val x = f(
                1,
                2, 3
            )
        """.trimIndent()
        assertThat(ArgumentListWrapping(Config.empty).lint(code)).hasSize(1)
    }

    @Test
    fun `does not report correct argument list wrapping`() {
        val code = """
            val x = f(
                1,
                2,
                3
            )
        """.trimIndent()
        assertThat(ArgumentListWrapping(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `does not report when overriding an indentation level config of 1`() {
        val code = """
            val x = f(
             1,
             2,
             3
            )
        """.trimIndent()
        val config = TestConfig("indentSize" to "1")
        assertThat(ArgumentListWrapping(config).lint(code)).isEmpty()
    }

    @Test
    fun `reports when max line length is exceeded`() {
        val code = """
            val x = f(1111, 2222, 3333)
        """.trimIndent()
        val config = TestConfig("maxLineLength" to "10")
        assertThat(ArgumentListWrapping(config).lint(code)).hasSize(4)
    }
}

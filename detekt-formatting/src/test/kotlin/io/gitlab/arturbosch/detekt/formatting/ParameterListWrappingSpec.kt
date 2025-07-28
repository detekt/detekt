package io.gitlab.arturbosch.detekt.formatting

import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.ParameterListWrapping
import dev.detekt.test.TestConfig
import dev.detekt.test.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ParameterListWrappingSpec {

    private lateinit var subject: ParameterListWrapping

    @BeforeEach
    fun createSubject() {
        subject = ParameterListWrapping(Config.empty)
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
            
            fun f(a: Int, b: Int, c: Int) {
            }
        """.trimIndent()
        val config = TestConfig("maxLineLength" to "10")
        assertThat(ParameterListWrapping(config).lint(code)).hasSize(4)
    }
}

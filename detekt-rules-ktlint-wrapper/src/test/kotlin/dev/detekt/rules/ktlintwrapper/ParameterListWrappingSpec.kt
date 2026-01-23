package dev.detekt.rules.ktlintwrapper

import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.wrappers.ParameterListWrapping
import dev.detekt.test.TestConfig
import dev.detekt.test.assertj.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ParameterListWrappingSpec {

    private lateinit var subject: ParameterListWrapping

    @BeforeEach
    fun createSubject() {
        subject = ParameterListWrapping(Config.Empty)
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
        val config = TestConfig("maxLineLength" to 10)
        assertThat(ParameterListWrapping(config).lint(code)).hasSize(4)
    }
}

package io.gitlab.arturbosch.detekt.formatting

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.assertThat
import dev.detekt.test.lint
import io.gitlab.arturbosch.detekt.formatting.wrappers.Indentation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class IndentationSpec {

    private lateinit var subject: Indentation

    @BeforeEach
    fun createSubject() {
        subject = Indentation(Config.empty)
    }

    @Nested
    inner class `indentation level equals 1` {

        val code = "fun main() {\n println()\n}"

        @Nested
        inner class `indentation level config of default` {

            @Test
            fun `reports wrong indentation level`() {
                assertThat(subject.lint(code)).hasSize(1)
            }

            @Test
            fun `places finding location to the indentation`() {
                assertThat(subject.lint(code))
                    .hasStartSourceLocation(2, 1)
                    .hasTextLocations(13 to 14)
            }
        }

        @Test
        fun `does not report when using an indentation level config of 1`() {
            val config = TestConfig("indentSize" to "1")
            assertThat(Indentation(config).lint(code)).isEmpty()
        }
    }

    @Test
    fun `does not report correct indentation level`() {
        val code = "fun main() {\n    println()\n}"
        assertThat(subject.lint(code)).isEmpty()
    }

    @Nested
    inner class `parameter list indent size equals 1` {

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
            assertThat(Indentation(config).lint(code)).isEmpty()
        }
    }
}

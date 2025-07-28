package io.gitlab.arturbosch.detekt.rules.empty

import dev.detekt.api.Config
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EmptyIfBlockSpec {

    private val subject = EmptyIfBlock(Config.empty)

    @Test
    fun `reports empty if with trailing semicolon`() {
        val code = """
            fun f() {
                var i = 0
                if (i == 0);
                i++
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports empty if with trailing semicolon on new line`() {
        val code = """
            fun f() {
                var i = 0
                if (i == 0)
                ;
                i++
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports empty if with trailing semicolon and braces`() {
        val code = """
            fun f() {
                var i = 0
                if (i == 0); {
                }
                i++
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report nonempty if with braces`() {
        val code = """
            fun f() {
                var i = 0
                if (i == 0) {
                    i++
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report nonempty if without braces`() {
        val code = """
            fun f() {
                var i = 0
                if (i == 0) i++
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report nonempty if without braces but semicolon`() {
        val code = """
            fun f() {
                var i = 0
                if (i == 0) i++;
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report empty if but nonempty else`() {
        val code = """
            fun f() {
                var i = 0
                if (i == 0) ;
                else i++
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report empty if and else-if but nonempty else`() {
        val code = """
            fun f() {
                var i = 0
                if (i == 0) ;
                else if (i == 1) ;
                else i++
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }
}

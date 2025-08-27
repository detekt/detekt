package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Test

class SafeCastSpec {
    val subject = SafeCast(Config.empty)

    @Test
    fun `reports negated expression`() {
        val code = """
            fun test(element: Int) {
                val cast = if (element !is Number) {
                    null
                } else {
                    element
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports expression`() {
        val code = """
            fun test(element: Int) {
                val cast = if (element is Number) {
                    element
                } else {
                    null
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports negated expression with no braces`() {
        val code = """
            fun test(element: Int) {
                val cast = if (element !is Number) null else element
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports expression with no braces`() {
        val code = """
            fun test(element: Int) {
                val cast = if (element is Number) element else null
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report wrong condition`() {
        val code = """
            fun test(element: Int) {
                val other = 3
                val cast = if (element == other) {
                    element
                } else {
                    null
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report wrong else clause`() {
        val code = """
            fun test(element: Int) {
                val cast = if (element is Number) {
                    element
                } else {
                    String()
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }
}

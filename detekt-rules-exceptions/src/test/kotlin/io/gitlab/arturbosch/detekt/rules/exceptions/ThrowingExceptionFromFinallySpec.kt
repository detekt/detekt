package io.gitlab.arturbosch.detekt.rules.exceptions

import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ThrowingExceptionFromFinallySpec {
    val subject = ThrowingExceptionFromFinally(Config.empty)

    @Test
    fun `should report a throw expression`() {
        val code = """
            fun x() {
                try {
                } finally {
                    if (1 == 1) {
                        throw IllegalArgumentException()
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `should report a nested throw expression`() {
        val code = """
            fun x() {
                try {
                } finally {
                    throw IllegalArgumentException()
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `should not report a finally expression without a throw expression`() {
        val code = """
            fun x() {
                try {
                } finally {
                    println()
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }
}

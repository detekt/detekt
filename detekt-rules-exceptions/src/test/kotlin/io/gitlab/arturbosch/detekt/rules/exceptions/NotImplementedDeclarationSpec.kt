package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NotImplementedDeclarationSpec {
    val subject = NotImplementedDeclaration()

    @Test
    fun `reports NotImplementedErrors`() {
        val code = """
            fun f() {
                if (1 == 1) throw NotImplementedError()
                throw NotImplementedError()
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(2)
    }

    @Test
    fun `reports TODO method calls`() {
        val code = """
            fun f() {
                TODO("not implemented")
                TODO()
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(2)
    }

    @Test
    fun `does not report TODO comments`() {
        val code = """
            fun f() {
                // TODO
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }
}

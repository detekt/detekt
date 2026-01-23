package dev.detekt.rules.exceptions

import dev.detekt.api.Config
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NotImplementedDeclarationSpec {
    val subject = NotImplementedDeclaration(Config.Empty)

    @Test
    fun `reports NotImplementedErrors`() {
        val code = """
            fun f() {
                if (1 == 1) throw NotImplementedError()
                throw NotImplementedError()
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(2)
    }

    @Test
    fun `reports TODO method calls`() {
        val code = """
            fun f() {
                TODO("not implemented")
                TODO()
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(2)
    }

    @Test
    fun `does not report TODO comments`() {
        val code = """
            fun f() {
                // TODO
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }
}

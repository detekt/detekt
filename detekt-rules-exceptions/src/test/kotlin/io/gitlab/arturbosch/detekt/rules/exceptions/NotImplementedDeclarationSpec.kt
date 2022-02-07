package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NotImplementedDeclarationSpec {
    val subject = NotImplementedDeclaration()

    @Nested
    inner class `NotImplementedDeclaration rule` {

        @Test
        fun `reports NotImplementedErrors`() {
            val code = """
            fun f() {
                if (1 == 1) throw NotImplementedError()
                throw NotImplementedError()
            }"""
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }

        @Test
        fun `reports TODO method calls`() {
            val code = """
            fun f() {
                TODO("not implemented")
                TODO()
            }"""
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }

        @Test
        fun `does not report TODO comments`() {
            val code = """
            fun f() {
                // TODO
            }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
}

package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class IfStatementCouldBeExpressionSpec {
    val subject = IfStatementCouldBeExpression(Config.empty)

    @Nested
    inner class `return in if` {
        @Test
        fun `does not report an expression`() {
            val code = """
                fun f(a: Int): Int {
                    return if (a > 0) {
                        1
                    } else {
                        -1
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `reports a statement`() {
            val code = """
                fun f(a: Int): Int {
                    if (a > 0) {
                        return 1
                    } else {
                        return -1
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `does not report a non exhaustive statement`() {
            val code = """
                fun f(a: Int): Int {
                    if (a > 0) {
                        return 1
                    }
                    return -1
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }

    @Nested
    inner class `assignment in if` {
        @Test
        fun `does not report an expression`() {
            val code = """
                fun f(a: Int) {
                    a = if (a > 0) {
                            1
                        } else if (a == 0) {
                            0        
                        } else {
                            -1
                        }
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `reports a statement`() {
            val code = """
                fun f(a: Int) {
                    if (a > 0) {
                        a = 1
                    } else if (a == 0) {
                        a = 0
                    } else {
                        a = -1
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `does not report a non exhaustive statement`() {
            val code = """
                fun f(a: Int) {
                    if (a > 0) {
                        a = 1
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report a statement that cannot be converted to an expression`() {
            val code = """
                fun f(a: Int, b: Int) {
                    if (a > 0) {
                        a = 1
                    } else {
                        b = 1
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
}

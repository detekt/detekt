package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class StatementCouldBeExpressionSpec(private val env: KotlinCoreEnvironment) {
    val subject = StatementCouldBeExpression(Config.empty)

    @Nested
    inner class `return in if and when` {
        @Test
        fun `does not report an if expression`() {
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
        fun `reports an if statement`() {
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
        fun `does not report a non exhaustive if statement`() {
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

        @Test
        fun `does not report a when expression`() {
            val code = """
            fun f(a: Int): Int {
                return when (a > 0) {
                    true -> 1
                    false -> -1
                }
            }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports a when statement`() {
            val code = """
            fun f(a: Int): Int {
                when (a > 0) {
                    true -> return 1
                    false -> return -1
                }
            }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report a non exhaustive when statement`() {
            val code = """
            fun f(a: Int): Int {
                when (a > 0) {
                    true -> return 1
                }
                return -1
            }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `assignment in if and when` {
        @Test
        fun `does not report an if expression`() {
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
        fun `reports an if statement`() {
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
        fun `does not report a non exhaustive if statement`() {
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
        fun `does not report an if statement that cannot be converted to an expression`() {
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

        @Test
        fun `does not report a when expression`() {
            val code = """
                fun f(a: Int) {
                    a = when (a > 0) {
                        true -> 1
                        false -> -1
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports a when statement`() {
            val code = """
                fun f(a: Int) {
                    when (a > 0) {
                        true -> a = 1
                        false  -> a = -1
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report a non exhaustive when statement`() {
            val code = """
                fun f(a: Int) {
                    when (a > 0) {
                        true -> a = 1
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report a when statement that cannot be converted to an expression`() {
            val code = """
                fun f(a: Int, b: Int) {
                    when (a > 0) {
                        true -> a = 1
                        false -> b = 1
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }
}

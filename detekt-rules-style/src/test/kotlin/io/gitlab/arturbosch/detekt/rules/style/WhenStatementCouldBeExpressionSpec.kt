package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class WhenStatementCouldBeExpressionSpec(private val env: KotlinCoreEnvironment) {
    val subject = WhenStatementCouldBeExpression(Config.empty)

    @Nested
    inner class `return in when` {
        @Test
        fun `does not report an expression`() {
            val code = """
            fun f(a: Int): Int {
                return when (a) {
                    1 -> 1
                    else -> 0
                }
            }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports a statement`() {
            val code = """
            fun f(a: Int): Int {
                when (a) {
                    1 -> return 1
                    else -> return 0
                }
            }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report a non exhaustive statement`() {
            val code = """
            fun f(a: Int): Int {
                when (a) {
                    1 -> return 1
                }
                return 0
            }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `assignment in when` {
        @Test
        fun `does not report an expression`() {
            val code = """
                var b = 42
                fun f(a: Int) {
                    a = when (a) {
                        1 -> 1
                        else -> 0
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports a statement`() {
            val code = """
                fun f(a: Int) {
                    var b = 42
                    when (a) {
                        1 -> b = 1
                        else  -> b = 0
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report a non exhaustive statement`() {
            val code = """
                var b = 42
                fun f(a: Int) {
                    when (a) {
                        1 -> b = 1
                    }
                    b = 0
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report a statement containing assignments to different variables`() {
            val code = """
                var b = 42
                var c = 36
                fun f(a: Int) {
                    when (a) {
                        1 -> b = 1
                        else -> c = 0
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report a statement containing different assignment operators`() {
            val code = """
                var b = 42
                fun f(a: Int) {
                    when (a) {
                        1 -> b = 1
                        else -> b -= 1
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }
}

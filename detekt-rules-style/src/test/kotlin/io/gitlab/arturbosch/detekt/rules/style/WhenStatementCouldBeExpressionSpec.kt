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
                return when (a > 0) {
                    true -> 1
                    false -> -1
                }
            }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports a statement`() {
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
        fun `does not report a non exhaustive statement`() {
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
    inner class `assignment in when` {
        @Test
        fun `does not report an expression`() {
            val code = """
                fun f(var a: Int) {
                    a = when (a > 0) {
                        true -> 1
                        false -> -1
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports a statement`() {
            val code = """
                fun f(var a: Int) {
                    when (a > 0) {
                        true -> a = 1
                        false  -> a = -1
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report a non exhaustive statement`() {
            val code = """
                fun f(var a: Int) {
                    when (a > 0) {
                        true -> a = 1
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report a statement that cannot be converted to an expression`() {
            val code = """
                fun f(var a: Int, var b: Int) {
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

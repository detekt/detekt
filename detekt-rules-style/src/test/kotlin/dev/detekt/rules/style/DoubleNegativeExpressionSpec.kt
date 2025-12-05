package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.KotlinEnvironmentContainer
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class DoubleNegativeExpressionSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = DoubleNegativeExpression(Config.empty)

    @Test
    fun not() {
        val code = """
            fun test(b: Boolean): Boolean {
                return b.not()
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `not not`() {
        val code = """
            fun test(b: Boolean): Boolean {
                return b.not().not()
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `not not not`() {
        val code = """
            fun test(b: Boolean): Boolean {
                return b.not().not().not()
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun exclamation() {
        val code = """
            fun test(b: Boolean): Boolean {
                return !b
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `double exclamation`() {
        val code = """
            fun test(b: Boolean): Boolean {
                return !!b
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `triple exclamation`() {
        val code = """
            fun test(b: Boolean): Boolean {
                return !!!b
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `exclamation not`() {
        val code = """
            fun test(b: Boolean): Boolean {
                return !b.not()
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `double exclamation not`() {
        val code = """
            fun test(b: Boolean): Boolean {
                return !!b.not()
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `implicit receiver`() {
        val code = """
            fun Boolean.test() {
                val x = !not()
                val y = not().not()
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(2)
    }
}

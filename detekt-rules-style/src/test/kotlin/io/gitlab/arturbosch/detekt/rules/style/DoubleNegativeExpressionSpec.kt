package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.test.utils.KotlinEnvironmentContainer
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lintWithContext
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

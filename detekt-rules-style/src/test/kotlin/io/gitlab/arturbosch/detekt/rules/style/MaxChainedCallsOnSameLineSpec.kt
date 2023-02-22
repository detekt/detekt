package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class MaxChainedCallsOnSameLineSpec(private val env: KotlinCoreEnvironment) {
    private val rule = MaxChainedCallsOnSameLine(TestConfig(mapOf("maxChainedCalls" to 3)))

    @Test
    fun `does not report 2 calls on a single line with a max of 3`() {
        val code = "val a = 0.plus(0)"

        assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report 3 calls on a single line with a max of 3`() {
        val code = "val a = 0.plus(0).plus(0)"

        assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports 4 calls on a single line with a max of 3`() {
        val code = "val a = 0.plus(0).plus(0).plus(0)"

        assertThat(rule.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports 4 safe qualified calls on a single line with a max of 3`() {
        val code = "val a = 0?.plus(0)?.plus(0)?.plus(0)"

        assertThat(rule.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports 4 non-null asserted calls on a single line with a max of 3`() {
        val code = "val a = 0!!.plus(0)!!.plus(0)!!.plus(0)"

        assertThat(rule.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports once for 7 calls on a single line with a max of 3`() {
        val code = "val a = 0.plus(0).plus(0).plus(0).plus(0).plus(0).plus(0)"

        assertThat(rule.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report 5 calls on separate lines with a max of 3`() {
        val code = """
            val a = 0
                .plus(0)
                .plus(0)
                .plus(0)
                .plus(0)
        """.trimIndent()

        assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report 3 calls on same line with wrapped calls with a max of 3`() {
        val code = """
            val a = 0.plus(0).plus(0)
                .plus(0).plus(0).plus(0)
                .plus(0).plus(0).plus(0)
        """.trimIndent()

        assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports 4 calls on same line with wrapped calls with a max of 3`() {
        val code = """
            val a = 0.plus(0).plus(0).plus(0)
                .plus(0)
                .plus(0)
        """.trimIndent()

        assertThat(rule.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports 4 calls on wrapped line with with a max of 3`() {
        val code = """
            val a = 0
                .plus(0)
                .plus(0).plus(0).plus(0).plus(0)
                .plus(0)
        """.trimIndent()

        assertThat(rule.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report long imports`() {
        val code = "import a.b.c.d.e"

        assertThat(rule.lint(code)).isEmpty()
    }

    @Test
    fun `does not report long package declarations`() {
        val code = "package a.b.c.d.e"

        assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not count package references as chained calls`() {
        val code = """
            val x = kotlin.math.floor(1.0).plus(1).plus(1)
        """.trimIndent()
        assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not count a package reference as chained calls`() {
        val code = """
            val x = kotlin.run { 1 }.plus(1).plus(1)
        """.trimIndent()
        assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
    }
}

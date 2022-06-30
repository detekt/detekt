package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Test

class MaxChainedCallsOnSameLineSpec {
    @Test
    fun `does not report 2 calls on a single line with a max of 3`() {
        val rule = MaxChainedCallsOnSameLine(TestConfig(mapOf("maxChainedCalls" to 3)))
        val code = "val a = 0.plus(0)"

        assertThat(rule.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report 3 calls on a single line with a max of 3`() {
        val rule = MaxChainedCallsOnSameLine(TestConfig(mapOf("maxChainedCalls" to 3)))
        val code = "val a = 0.plus(0).plus(0)"

        assertThat(rule.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `reports 4 calls on a single line with a max of 3`() {
        val rule = MaxChainedCallsOnSameLine(TestConfig(mapOf("maxChainedCalls" to 3)))
        val code = "val a = 0.plus(0).plus(0).plus(0)"

        assertThat(rule.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports 4 safe qualified calls on a single line with a max of 3`() {
        val rule = MaxChainedCallsOnSameLine(TestConfig(mapOf("maxChainedCalls" to 3)))
        val code = "val a = 0?.plus(0)?.plus(0)?.plus(0)"

        assertThat(rule.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports 4 non-null asserted calls on a single line with a max of 3`() {
        val rule = MaxChainedCallsOnSameLine(TestConfig(mapOf("maxChainedCalls" to 3)))
        val code = "val a = 0!!.plus(0)!!.plus(0)!!.plus(0)"

        assertThat(rule.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports once for 7 calls on a single line with a max of 3`() {
        val rule = MaxChainedCallsOnSameLine(TestConfig(mapOf("maxChainedCalls" to 3)))
        val code = "val a = 0.plus(0).plus(0).plus(0).plus(0).plus(0).plus(0)"

        assertThat(rule.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `does not report 5 calls on separate lines with a max of 3`() {
        val rule = MaxChainedCallsOnSameLine(TestConfig(mapOf("maxChainedCalls" to 3)))
        val code = """
            val a = 0
                .plus(0)
                .plus(0)
                .plus(0)
                .plus(0)
        """

        assertThat(rule.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report 3 calls on same line with wrapped calls with a max of 3`() {
        val rule = MaxChainedCallsOnSameLine(TestConfig(mapOf("maxChainedCalls" to 3)))
        val code = """
            val a = 0.plus(0).plus(0)
                .plus(0).plus(0).plus(0)
                .plus(0).plus(0).plus(0)
        """

        assertThat(rule.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `reports 4 calls on same line with wrapped calls with a max of 3`() {
        val rule = MaxChainedCallsOnSameLine(TestConfig(mapOf("maxChainedCalls" to 3)))
        val code = """
            val a = 0.plus(0).plus(0).plus(0)
                .plus(0)
                .plus(0)
        """

        assertThat(rule.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports 4 calls on wrapped line with with a max of 3`() {
        val rule = MaxChainedCallsOnSameLine(TestConfig(mapOf("maxChainedCalls" to 3)))
        val code = """
            val a = 0
                .plus(0)
                .plus(0).plus(0).plus(0).plus(0)
                .plus(0)
        """

        assertThat(rule.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `does not report long imports`() {
        val rule = MaxChainedCallsOnSameLine(TestConfig(mapOf("maxChainedCalls" to 3)))
        val code = "import a.b.c.d.e"

        assertThat(rule.lint(code)).isEmpty()
    }

    @Test
    fun `does not report long package declarations`() {
        val rule = MaxChainedCallsOnSameLine(TestConfig(mapOf("maxChainedCalls" to 3)))
        val code = "package a.b.c.d.e"

        assertThat(rule.lint(code)).isEmpty()
    }
}

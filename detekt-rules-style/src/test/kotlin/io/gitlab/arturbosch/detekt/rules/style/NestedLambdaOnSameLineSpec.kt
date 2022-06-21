package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class NestedLambdaOnSameLineSpec {
    private val subject = NestedLambdaOnSameLine(Config.empty)

    @Test
    fun `reports nested lambdas on same line`() {
        val code = """
            val a = listOf("")
                .map { it.map { it } }
        """

        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports nested lambdas with newline after body`() {
        val code = """
            val a = listOf("")
                .map { it.map { it }
                }
        """

        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports nested lambdas on same line where inner lambda has multiple lines`() {
        val code = """
            val a = listOf("")
                .map { it.map {
                    it } }
        """

        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `does not report nested lambdas on different lines`() {
        val code = """
            val a = listOf("")
                .map {
                    it.map { it }
                }
        """

        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report single line lambda without nested lambda`() {
        val code = """
            val a = listOf("")
                .map { 0 }
        """

        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report single line lambda with comment containing braces`() {
        val code = """
            val a = listOf("")
                .map { 0 /* { x -> y } */ }
        """

        assertThat(subject.compileAndLint(code)).isEmpty()
    }
}

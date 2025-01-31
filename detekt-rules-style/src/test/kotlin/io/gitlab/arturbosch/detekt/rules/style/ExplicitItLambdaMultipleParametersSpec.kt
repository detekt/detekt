package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ExplicitItLambdaMultipleParametersSpec {
    val subject = ExplicitItLambdaMultipleParameters(Config.empty)

    @Nested
    inner class `multiple parameters one of which with name 'it' declared explicitly` {
        @Test
        fun `reports when parameter types are not declared`() {
            val findings =
                subject.compileAndLint(
                    """
                    fun f() {
                        val flat = listOf(listOf(1), listOf(2)).mapIndexed { index, it -> it + index }
                    }
                    """.trimIndent(),
                )
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports when parameter types are declared explicitly`() {
            val findings =
                subject.compileAndLint(
                    """
                    fun f() {
                        val lambda = { it: Int, that: String -> it.toString() + that }
                    }
                    """.trimIndent(),
                )
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not report when parameter type with is declared explicitly for multi params un-inferrable lambda`() {
            val findings =
                subject.compileAndLint(
                    """
                    fun f(): (Int, Int) -> Int {
                        return { value: Int, a: Int -> (value + a).inc() }::invoke
                    }
                    """.trimIndent(),
                )
            assertThat(findings).isEmpty()
        }

        @Test
        fun `reports when parameter type with name it when declared explicitly for multi params un-inferrable lambda`() {
            val findings =
                subject.compileAndLint(
                    """
                    fun f(): (Int, Int) -> Int {
                        return { it: Int, a: Int -> (it + a).inc() }::invoke
                    }
                    """.trimIndent(),
                )
            assertThat(findings).hasSize(1)
            assertThat(findings[0]).hasMessage("`it` should not be used as name for a lambda parameter.")
        }
    }
}

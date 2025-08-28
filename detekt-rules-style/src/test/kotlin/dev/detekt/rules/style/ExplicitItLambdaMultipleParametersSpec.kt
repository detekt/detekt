package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ExplicitItLambdaMultipleParametersSpec {
    val subject = ExplicitItLambdaMultipleParameters(Config.empty)

    @Nested
    inner class `multiple parameters one of which with name 'it' declared explicitly` {
        @Test
        fun `reports when parameter types are not declared`() {
            val findings =
                subject.lint(
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
                subject.lint(
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
                subject.lint(
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
                subject.lint(
                    """
                    fun f(): (Int, Int) -> Int {
                        return { it: Int, a: Int -> (it + a).inc() }::invoke
                    }
                    """.trimIndent(),
                )
            assertThat(findings).singleElement()
                .hasMessage("`it` should not be used as name for a lambda parameter.")
        }
    }

    @Nested
    inner class `no parameter declared explicitly` {
        @Test
        fun `does not report implicit 'it' parameter usage`() {
            val findings =
                subject.lint(
                    """
                    fun f() {
                        val lambda = { i: Int -> i.toString() }
                        val digits = 1234.let { lambda(it) }.toList()
                        val flat = listOf(listOf(1), listOf(2)).flatMap { it }
                    }
                    """.trimIndent(),
                )
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `single parameter lambda with name 'it' declared explicitly` {
        @Test
        fun `does not report explicit 'it' parameter usage in one parameter`() {
            val findings =
                subject.lint(
                    """
                    fun f() {
                        val digits = 1234.let { it -> listOf(it) }
                    }
                    """.trimIndent(),
                )
            assertThat(findings).isEmpty()
        }
    }
}

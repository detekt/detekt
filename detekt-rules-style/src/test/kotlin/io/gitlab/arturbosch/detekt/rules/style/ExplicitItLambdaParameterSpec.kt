package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ExplicitItLambdaParameterSpec {
    val subject = ExplicitItLambdaParameter(Config.empty)

    @Nested
    inner class `single parameter lambda with name 'it' declared explicitly` {
        @Test
        fun `reports when parameter type is not declared`() {
            val findings =
                subject.compileAndLint(
                    """
                    fun f() {
                        val digits = 1234.let { it -> listOf(it) }
                    }
                    """.trimIndent(),
                )
            assertThat(findings).hasSize(1)
            assertThat(findings[0]).hasMessage(
                "This explicit usage of `it` as the lambda parameter name can be omitted.",
            )
        }

        @Test
        fun `reports when parameter type is declared explicitly`() {
            val findings =
                subject.compileAndLint(
                    """
                    fun f() {
                        val lambda = { it: Int -> it.toString() }
                    }
                    """.trimIndent(),
                )
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not reports when parameter type is declared explicitly when variable name is not it`() {
            val findings =
                subject.compileAndLint(
                    """
                    fun f() {
                        val lambda = { value: Int -> value.toString() }
                    }
                    """.trimIndent(),
                )
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when parameter type is declared explicitly for un-inferrable lambda`() {
            val findings =
                subject.compileAndLint(
                    """
                    fun f1(): (Int) -> Int {
                        return { value: Int -> value.inc() }::invoke
                    }

                    fun f2(): (Int) -> Int {
                        return { value: Int -> value.inc() }::invoke
                    }

                    fun f3(): (((Int) -> Int) -> Unit) -> (Int) -> Int {
                        return { value: Int -> value.inc() }::also
                    }
                    """.trimIndent(),
                )
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when parameter type is declared explicitly for un-inferrable lambda wrapped in paren`() {
            val findings =
                subject.compileAndLint(
                    """
                    fun f(): (Int) -> Int {
                        return ({ value: Int -> value.inc() })::invoke
                    }
                    """.trimIndent(),
                )
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `no parameter declared explicitly` {
        @Test
        fun `does not report implicit 'it' parameter usage`() {
            val findings =
                subject.compileAndLint(
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
}

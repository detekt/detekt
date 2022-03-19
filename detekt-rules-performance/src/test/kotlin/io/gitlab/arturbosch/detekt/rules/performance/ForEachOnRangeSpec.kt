package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ForEachOnRangeSpec {

    val subject = ForEachOnRange()

    @Nested
    inner class `ForEachOnRange rule` {

        @Nested
        inner class `using a forEach on a range` {
            val code = """
            fun test() {
                (1..10).forEach {
                    println(it)
                }
                (1 until 10).forEach {
                    println(it)
                }
                (10 downTo 1).forEach {
                    println(it)
                }
                (10 downTo 1 step 2).forEach {
                    println(it)
                }
            }
            """

            @Test
            fun `should report the forEach usage`() {
                val findings = subject.compileAndLint(code)
                assertThat(findings).hasSize(4)
            }
        }

        @Nested
        inner class `using any other method on a range` {
            val code = """
            fun test() {
                (1..10).isEmpty()
            }
            """

            @Test
            fun `should not report any issues`() {
                val findings = subject.compileAndLint(code)
                assertThat(findings).isEmpty()
            }
        }

        @Nested
        inner class `using a forEach on a list` {
            val code = """
            fun test() {
                listOf(1, 2, 3).forEach {
                    println(it)
                }
            }
            """

            @Test
            fun `should not report any issues`() {
                val findings = subject.compileAndLint(code)
                assertThat(findings).isEmpty()
            }
        }
    }
}

package io.gitlab.arturbosch.detekt.rules.performance

import dev.detekt.api.Config
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ForEachOnRangeSpec {

    val subject = ForEachOnRange(Config.empty)

    @Nested
    inner class `using a forEach on a range` {
        val code = """
            fun test() {
                (1..10).forEach {
                    println(it)
                }
                (1.rangeTo(10)).forEach {
                    println(it)
                }
                @OptIn(ExperimentalStdlibApi::class)
                (1..<10).forEach {
                    println(it)
                }
                (1 until 10).forEach {
                    println(it)
                }
                (1.until(10)).forEach {
                    println(it)
                }
                ((1 until 10).reversed()).forEach {
                    println(it)
                }
                (10 downTo 1).forEach {
                    println(it)
                }
                (10.downTo(1)).forEach {
                    println(it)
                }
                (10 downTo 1 step 2).forEach {
                    println(it)
                }
                (10.downTo(1).step(2)).forEach {
                    println(it)
                }
                (10.downTo(1).step(2).reversed()).forEach {
                    println(it)
                }
                ((10 downTo 1 step 2).reversed()).forEach {
                    println(it)
                }
                ((10 downTo 1 step 2).reversed() step 2).forEach {
                    println(it)
                }
                (1..10).reversed().forEach { 
                    println(it)
                }
                (1..10).reversed().step(2).forEach { 
                    println(it)
                }
            }
        """.trimIndent()

        @Test
        fun `should report the forEach usage`() {
            val findings = subject.lint(code)
            assertThat(findings).hasSize(15)
        }

        @Test
        fun `should report the forEach usage for other type ranges`() {
            val code = """
                fun test() {
                    (1L..10L).forEach { 
                        println(it)
                    }
                    (1U..10U).forEach { 
                        println(it)
                    }
                    ('0'..'9').forEach { 
                        println(it)
                    }
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).hasSize(3)
        }
    }

    @Nested
    inner class `using any other method on a range` {
        val code = """
            fun test() {
                (1..10).isEmpty()
            }
        """.trimIndent()

        @Test
        fun `should not report any issues`() {
            val findings = subject.lint(code)
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
                listOf(1, 2, 3).also { 1..10 }.forEach {
                    println(it)
                }
            }
        """.trimIndent()

        @Test
        fun `should not report any issues`() {
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }
    }
}

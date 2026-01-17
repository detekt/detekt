package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val MAX_DESTRUCTURING_ENTRIES = "maxDestructuringEntries"

class DestructuringDeclarationWithTooManyEntriesSpec {
    val subject = DestructuringDeclarationWithTooManyEntries(Config.empty)

    @Nested
    inner class `default configuration` {

        @Test
        fun `does not report destructuring declarations with 2 or 3 entries`() {
            val code = """
                fun testFun() {
                    val (x, y) = Pair(3, 4)
                    println(x)
                    println(y)
                
                    val (a, b, c) = Triple(1, 2, 3)
                    println(a)
                    println(b)
                    println(c)
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports destructuring declarations with more than 3 entries`() {
            val code = """
                fun testFun() {
                    data class ManyElements(val a: Int, val b: Int, val c: Int, val d: Int)
                
                    val (a, b, c, d) = ManyElements(1, 2, 3, 4)
                    println(a)
                    println(b)
                    println(c)
                    println(d)
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `does not report destructuring declarations in lambdas with 2 or 3 entries`() {
            val code = """
                fun testFun() {
                    val items = listOf(Pair(3, 4))
                
                    items.forEach { (a, b) ->
                        println(a)
                        println(b)
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports destructuring declarations in lambdas with more than 3 entries`() {
            val code = """
                fun testFun() {
                    data class ManyElements(val a: Int, val b: Int, val c: Int, val d: Int)
                
                    val items = listOf(ManyElements(1, 2, 3, 4))
                    items.forEach { (a, b, c, d) ->
                        println(a)
                        println(b)
                        println(c)
                        println(d)
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }
    }

    @Nested
    inner class `maxDestructuringEntries = 2` {

        val configuredRule =
            DestructuringDeclarationWithTooManyEntries(
                TestConfig(MAX_DESTRUCTURING_ENTRIES to 2)
            )

        @Test
        fun `does not report destructuring declarations with 2 entries`() {
            val code = """
                fun testFun() {
                    val (x, y) = Pair(3, 4)
                    println(x)
                    println(y)
                }
            """.trimIndent()
            assertThat(configuredRule.lint(code)).isEmpty()
        }

        @Test
        fun `reports destructuring declarations with more than 2 entries`() {
            val code = """
                fun testFun() {
                    val (a, b, c) = Triple(1, 2, 3)
                    println(a)
                    println(b)
                    println(c)
                }
            """.trimIndent()
            assertThat(configuredRule.lint(code)).hasSize(1)
        }
    }
}

package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class DestructuringDeclarationWithTooManyEntriesSpec : Spek({
    val subject by memoized { DestructuringDeclarationWithTooManyEntries() }

    describe("DestructuringDeclarationWithTooManyEntries rule") {

        context("default configuration") {

            it("does not report destructuring declarations with 2 or 3 entries") {
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
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("reports destructuring declarations with more than 3 entries") {
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
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("does not report destructuring declarations in lambdas with 2 or 3 entries") {
                val code = """
                fun testFun() {
                    val items = listOf(Pair(3, 4))

                    items.forEach { (a, b) ->
                        println(a)
                        println(b)
                    }
                }
            """.trimIndent()
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("reports destructuring declarations in lambdas with more than 3 entries") {
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
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }
        }

        context("maxDestructuringEntries = 2") {

            val configuredRule by memoized {
                DestructuringDeclarationWithTooManyEntries(
                    TestConfig(mapOf(DestructuringDeclarationWithTooManyEntries.MAX_DESTRUCTURING_ENTRIES to "2"))
                )
            }

            it("does not report destructuring declarations with 2 entries") {
                val code = """
                fun testFun() {
                    val (x, y) = Pair(3, 4)
                    println(x)
                    println(y)
                }
            """.trimIndent()
                assertThat(configuredRule.compileAndLint(code)).isEmpty()
            }

            it("reports destructuring declarations with more than 2 entries") {
                val code = """
                fun testFun() {
                    val (a, b, c) = Triple(1, 2, 3)
                    println(a)
                    println(b)
                    println(c)
                }
            """.trimIndent()
                assertThat(configuredRule.compileAndLint(code)).hasSize(1)
            }
        }
    }
})

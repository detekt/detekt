package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class OptionalWhenBracesSpec : Spek({
    val subject by memoized { OptionalWhenBraces() }

    describe("check optional braces in when expression") {

        it("does not report necessary braces") {
            val code = """
                fun x() {
                    when (1) {
                        1 -> print(1)
                        2 -> {
                            print(2)
                            print(2)
                        }
                        else -> {
                            // a comment
                            println()
                        }
                    }
                }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("reports unnecessary braces") {
            val code = """
                fun x() {
                    when (1) {
                        1 -> { print(1) }
                        else -> println()
                    }
                }"""
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        context("the statement is a lambda expression") {
            it("does not report if the lambda has no arrow") {
                val code = """
                    fun test(b: Boolean): (Int) -> Int {
                        return when (b) {
                            true -> { { it + 100 } }
                            false -> { { it + 200  } }
                        }
                    }
                """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("reports if the lambda has an arrow") {
                val code = """
                    fun test(b: Boolean): (Int) -> Int {
                        return when (b) {
                            true -> { { i -> i + 100 } }
                            false -> { { i -> i + 200  } }
                        }
                    }
                """
                assertThat(subject.compileAndLint(code)).hasSize(2)
            }
        }
    }
})

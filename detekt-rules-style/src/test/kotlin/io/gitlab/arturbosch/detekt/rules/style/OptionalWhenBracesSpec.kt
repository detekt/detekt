package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
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

        it("reports unnecessary braces for nested when") {
            val code = """
                import kotlin.random.Random
                
                fun main() {
                    when(Random.nextBoolean()) {
                        true -> {
                            when(Random.nextBoolean()) {
                                true -> {
                                    println("true")
                                }
                                false -> {
                                    println("false")
                                }
                            }
                            println("end")
                        }
                        false -> println("false")
                    }
                }
            """
            assertThat(subject.compileAndLint(code))
                .hasSize(2)
                .hasSourceLocations(SourceLocation(7, 17), SourceLocation(10, 17))
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

package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.getFunctionByName
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class CyclomaticComplexitySpec : Spek({

    val defaultFunctionComplexity = 1

    describe("ignoreSimpleWhenEntries is false") {

        it("counts simple when branches as 1") {
            val function = compileContentForTest("""
                fun test() {
                    when (System.currentTimeMillis()) {
                        0 -> println("Epoch!")
                        1 -> println("1 past epoch.")
                        else -> println("Meh")
                    }
                }
            """).getFunctionByName("test")

            val actual = CyclomaticComplexity.calculate(function) {
                ignoreSimpleWhenEntries = false
            }

            assertThat(actual).isEqualTo(defaultFunctionComplexity + 3)
        }

        it("counts block when branches as 1") {
            val function = compileContentForTest("""
                fun test() {
                    when (System.currentTimeMillis()) {
                        0 -> {
                            println("Epoch!")
                        }
                        1 -> println("1 past epoch.")
                        else -> println("Meh")
                    }
                }
            """).getFunctionByName("test")

            val actual = CyclomaticComplexity.calculate(function) {
                ignoreSimpleWhenEntries = false
            }

            assertThat(actual).isEqualTo(defaultFunctionComplexity + 3)
        }
    }

    describe("ignoreSimpleWhenEntries is true") {

        it("counts a when with only simple branches as 1") {
            val function = compileContentForTest("""
                fun test() {
                    when (System.currentTimeMillis()) {
                        0 -> println("Epoch!")
                        1 -> println("1 past epoch.")
                        else -> println("Meh")
                    }
                }
            """).getFunctionByName("test")

            val actual = CyclomaticComplexity.calculate(function) {
                ignoreSimpleWhenEntries = true
            }

            assertThat(actual).isEqualTo(defaultFunctionComplexity + 1)
        }

        it("does not count simple when branches") {
            val function = compileContentForTest("""
                fun test() {
                    when (System.currentTimeMillis()) {
                        0 -> {
                            println("Epoch!")
                            println("yay")
                        }
                        1 -> {
                            println("1 past epoch!")
                        }
                        else -> println("Meh")
                    }
                }
            """).getFunctionByName("test")

            val actual = CyclomaticComplexity.calculate(function) {
                ignoreSimpleWhenEntries = true
            }

            assertThat(actual).isEqualTo(defaultFunctionComplexity + 2)
        }

        it("counts block when branches as 1") {
            val function = compileContentForTest("""
                fun test() {
                    when (System.currentTimeMillis()) {
                        0 -> {
                            println("Epoch!")
                            println("yay!")
                        }
                        1 -> {
                            println("1 past epoch.")
                            println("yay?")
                        }
                        2 -> println("shrug")
                        else -> println("Meh")
                    }
                }
            """).getFunctionByName("test")

            val actual = CyclomaticComplexity.calculate(function) {
                ignoreSimpleWhenEntries = true
            }

            assertThat(actual).isEqualTo(defaultFunctionComplexity + 2)
        }
    }
})

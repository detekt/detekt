package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.getFunctionByName
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class CyclomaticComplexitySpec : Spek({

    val defaultFunctionComplexity = 1

    describe("basic function expressions are tested") {

        it("counts for safe navigation") {
            val code = compileContentForTest("""
                    fun test() = null as? String ?: ""
                """.trimIndent())

            val actual = CyclomaticComplexity.calculate(code)

            assertThat(actual).isEqualTo(defaultFunctionComplexity + 1)
        }

        it("counts if and && and || expressions") {
            val code = compileContentForTest("""
                    fun test() = if (true || true && false) 1 else 0
                """.trimIndent())

            val actual = CyclomaticComplexity.calculate(code)

            assertThat(actual).isEqualTo(defaultFunctionComplexity + 3)
        }

        it("counts while, continue and break") {
            val code = compileContentForTest("""
                    fun test(i: Int) {
                        var j = i
                        while(true) { // 1
                            if (j == 5) { // 1
                                continue // 1
                            } else if (j == 2) { // 1
                                break // 1
                            } else {
                                j += i
                            }
                        }
                        println("finished")
                    }
                """.trimIndent())

            val actual = CyclomaticComplexity.calculate(code)

            assertThat(actual).isEqualTo(defaultFunctionComplexity + 5)
        }
    }

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

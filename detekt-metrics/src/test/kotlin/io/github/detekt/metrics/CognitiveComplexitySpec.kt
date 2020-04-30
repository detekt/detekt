package io.github.detekt.metrics

import io.gitlab.arturbosch.detekt.test.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class CognitiveComplexitySpec : Spek({

    describe("cognitive complexity") {

        it("sums seven for sumOfPrimes example") {
            val code = compileContentForTest("""
                fun sumOfPrimes(max: Int): Int {
                    var total = 0
                    next@ for (i in 1..max) {
                        for (j in 2 until i) {
                            if (i % j == 0) {
                                continue@next
                            }
                        }
                        println(i)
                        total++
                    }
                    return total
                }
            """)

            assertThat(CognitiveComplexity.calculate(code)).isEqualTo(7)
        }

        it("sums one for getWords example for a single when expression") {
            val code = compileContentForTest("""
                 fun getWords(number: Int): String = when (number) {
                     1 -> "one"
                     2 -> "a couple"
                     3 -> "a few"
                     else -> "lots"
                 }
             """.trimIndent())

            assertThat(CognitiveComplexity.calculate(code)).isEqualTo(1)
        }

        it("adds one for recursion") {
            val code = compileContentForTest("""
                fun factorial(n: Int): Int =
                    if (n >= 1) n * factorial(n - 1) else 1
            """.trimIndent())

            assertThat(CognitiveComplexity.calculate(code)).isEqualTo(2)
        }

        it("ignores shorthand operators") {
            val code = compileContentForTest("""
                fun parse(args: Array<String>): Nothing = TODO()
                fun main(args: Array<String>) {
                   args.takeIf { it.size > 3 }?.let(::parse) ?: error("not enough arguments")
                }
            """.trimIndent())

            assertThat(CognitiveComplexity.calculate(code)).isEqualTo(0)
        }

        it("adds one per catch clause") {
            val code = compileContentForTest("""
                fun main() {
                    try {
                    } catch (e: IllegalArgumentException) {
                    } catch (e: IllegalStateException) {
                    } catch (e: Throwable) {}
                }
            """.trimIndent())

            assertThat(CognitiveComplexity.calculate(code)).isEqualTo(3)
        }

        it("adds extra complexity for nesting") {
            val code = compileContentForTest("""
                fun main() {
                    try {
                        if (true) { // +1
                            for (i in 0..10) { // +2
                                while(true) { // +3
                                    // more code
                                }
                            }
                        }
                    } catch (e: Exception) { // +1
                        do {} while(true) // +2
                    }
                }
            """.trimIndent())

            assertThat(CognitiveComplexity.calculate(code)).isEqualTo(9)
        }

        it("adds nesting for lambdas but not complexity") {
            val code = compileContentForTest("""
                fun main() { run { if (true) {} } }
            """.trimIndent())

            assertThat(CognitiveComplexity.calculate(code)).isEqualTo(2)
        }

        it("adds nesting for nested functions but not complexity") {
            val code = compileContentForTest("""
                fun main() { fun run() { if (true) {} } }
            """.trimIndent())

            assertThat(CognitiveComplexity.calculate(code)).isEqualTo(2)
        }
    }
})

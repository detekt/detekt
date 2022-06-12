package io.github.detekt.metrics

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CognitiveComplexitySpec {

    @Test
    fun `sums seven for sumOfPrimes example`() {
        val code = compileContentForTest(
            """
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
            """
        )

        assertThat(CognitiveComplexity.calculate(code)).isEqualTo(7)
    }

    @Test
    fun `sums one for getWords example for a single when expression`() {
        val code = compileContentForTest(
            """
             fun getWords(number: Int): String = when (number) {
                 1 -> "one"
                 2 -> "a couple"
                 3 -> "a few"
                 else -> "lots"
             }
            """
        )

        assertThat(CognitiveComplexity.calculate(code)).isEqualTo(1)
    }

    @Nested
    inner class `recursion` {

        @Test
        fun `adds one for recursion inside class`() {
            val code = compileContentForTest(
                """
                class A {
                    fun factorial(n: Int): Int =
                        if (n >= 1) n * this.factorial(n - 1) else 1
                }
                """
            )

            assertThat(CognitiveComplexity.calculate(code)).isEqualTo(2)
        }

        @Test
        fun `adds one for top level recursion`() {
            val code = compileContentForTest(
                """
                fun factorial(n: Int): Int =
                    if (n >= 1) n * factorial(n - 1) else 1
                """
            )

            assertThat(CognitiveComplexity.calculate(code)).isEqualTo(2)
        }

        @Test
        fun `does not add as it is only the same name`() {
            val code = compileContentForTest(
                """
                object O { fun factorial(i: Int): Int = i - 1 }
                fun factorial(n: Int): Int =
                    if (n >= 1) n * O.factorial(n - 1) else 1
                """
            )

            assertThat(CognitiveComplexity.calculate(code)).isEqualTo(1)
        }
    }

    @Test
    fun `ignores shorthand operators`() {
        val code = compileContentForTest(
            """
            fun parse(args: Array<String>): Nothing = TODO()
            fun main(args: Array<String>) {
               args.takeIf { it.size > 3 }?.let(::parse) ?: error("not enough arguments")
            }
            """
        )

        assertThat(CognitiveComplexity.calculate(code)).isEqualTo(0)
    }

    @Test
    fun `adds one per catch clause`() {
        val code = compileContentForTest(
            """
            fun main() {
                try {
                } catch (e: IllegalArgumentException) {
                } catch (e: IllegalStateException) {
                } catch (e: Throwable) {}
            }
            """
        )

        assertThat(CognitiveComplexity.calculate(code)).isEqualTo(3)
    }

    @Test
    fun `adds extra complexity for nesting`() {
        val code = compileContentForTest(
            """
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
            """
        )

        assertThat(CognitiveComplexity.calculate(code)).isEqualTo(9)
    }

    @Test
    fun `adds nesting for lambdas but not complexity`() {
        val code = compileContentForTest(
            """
            fun main() { run { if (true) {} } }
            """
        )

        assertThat(CognitiveComplexity.calculate(code)).isEqualTo(2)
    }

    @Test
    fun `adds nesting for nested functions but not complexity`() {
        val code = compileContentForTest(
            """
            fun main() { fun run() { if (true) {} } }
            """
        )

        assertThat(CognitiveComplexity.calculate(code)).isEqualTo(2)
    }

    @Nested
    inner class `binary expressions` {

        @Test
        fun `does not increment on just a condition`() {
            val code = compileContentForTest(
                """
                fun test(cond_ Boolean) = !cond
                """
            )

            assertThat(CognitiveComplexity.calculate(code)).isEqualTo(0)
        }

        @Nested
        inner class `increments for every non-like operator` {

            @Test
            fun `adds one for just a &&`() {
                val code = compileContentForTest(
                    """
                    fun test(cond_ Boolean) = !cond && !cond
                    """
                )

                assertThat(CognitiveComplexity.calculate(code)).isEqualTo(1)
            }

            @Test
            fun `adds only one for repeated &&`() {
                val code = compileContentForTest(
                    """
                    fun test(cond_ Boolean) = !cond && !cond && !cond
                    """
                )

                assertThat(CognitiveComplexity.calculate(code)).isEqualTo(1)
            }

            @Test
            fun `adds one per logical alternate operator`() {
                val code = compileContentForTest(
                    """
                    fun test(cond_ Boolean) = !cond && !cond || cond
                    """
                )

                assertThat(CognitiveComplexity.calculate(code)).isEqualTo(2)
            }

            @Test
            fun `adds one per logical alternate operator with like operators in between`() {
                val code = compileContentForTest(
                    """
                    fun test(cond_ Boolean) {
                        if (                    // +1
                            !cond 
                            && !cond && !cond   // +1
                            || cond || cond     // +1
                            && cond             // +1
                        ) {}
                    }
                    """
                )

                assertThat(CognitiveComplexity.calculate(code)).isEqualTo(4)
            }

            @Test
            fun `adds one for negated but similar operators`() {
                val code = compileContentForTest(
                    """
                    fun test(cond_ Boolean) {
                        if (                    // +1
                            !cond 
                            && !(cond && cond)  // +2
                        ) {}
                    }
                    """
                )

                assertThat(CognitiveComplexity.calculate(code)).isEqualTo(3)
            }

            @Test
            fun `adds only one for a negated chain of similar operators`() {
                val code = compileContentForTest(
                    """
                    fun test(cond_ Boolean) {
                        if (                    // +1
                            !cond 
                            && !(cond && cond && cond)  // +2
                        ) {}
                    }
                    """
                )

                assertThat(CognitiveComplexity.calculate(code)).isEqualTo(3)
            }

            @Test
            fun `adds one for every negated similar operator chain`() {
                val code = compileContentForTest(
                    """
                    fun test(cond_ Boolean) {
                        if (                            // +1
                            !cond 
                            && !(cond && cond && cond)  // +2
                            || !(cond || cond)          // +2
                        ) {}
                    }
                    """
                )

                assertThat(CognitiveComplexity.calculate(code)).isEqualTo(5)
            }
        }
    }
}

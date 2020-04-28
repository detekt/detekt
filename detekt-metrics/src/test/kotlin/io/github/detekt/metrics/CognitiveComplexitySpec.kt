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

            val actual = CognitiveComplexity.calculate(code)

            assertThat(actual).isEqualTo(7)
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

            val actual = CognitiveComplexity.calculate(code)

            assertThat(actual).isEqualTo(1)
        }
    }
})

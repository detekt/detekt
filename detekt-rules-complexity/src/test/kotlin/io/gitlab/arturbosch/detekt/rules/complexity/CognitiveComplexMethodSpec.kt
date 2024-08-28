package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class CognitiveComplexMethodSpec {

    private val testConfig = TestConfig("allowedComplexity" to "1")

    @Test
    fun `should report complex function exceeding the allowed complexity`() {
        val code = """
            fun sumOfPrimes(max: Int): Int { // total cognitive complexity is 7
                var total = 0
                next@ for (i in 1..max) { // +1
                    for (j in 2 until i) { // +1 +1
                        if (i % j == 0) { // +1 +2
                            continue@next // +1
                        }
                    }
                    println(i)
                    total++
                }
                return total
            }
        """.trimIndent()
        val findings = CognitiveComplexMethod(testConfig).compileAndLint(code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasStartSourceLocations(SourceLocation(1, 5))
    }

    @Test
    fun `should not report function that has exactly the allowed complexity`() {
        val code = """
            fun divide(a: Int, b: Int): Int { // total cognitive complexity is 1
                if(b != 0) {
                    return a / b
                }
                return 0
            }
        """.trimIndent()
        val findings = CognitiveComplexMethod(testConfig).compileAndLint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should not report function that has less than the allowed complexity`() {
        val code = """
            fun add(a: Int, b: Int): Int { // total cognitive complexity is 0
                return a + b
            }
        """.trimIndent()
        val findings = CognitiveComplexMethod(testConfig).compileAndLint(code)
        assertThat(findings).isEmpty()
    }
}

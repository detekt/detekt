package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.isThresholded
import org.junit.jupiter.api.Test

class CognitiveComplexMethodSpec {
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
               
               fun simpleTest(): Boolean {
                   return true
               }
    """.trimIndent()

    @Test
    fun `should report complex function`() {
        val findings = CognitiveComplexMethod(TestConfig("threshold" to "1")).compileAndLint(code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasStartSourceLocations(SourceLocation(1, 5))
        assertThat(findings.first()).isThresholded().withValue(7)
    }
}

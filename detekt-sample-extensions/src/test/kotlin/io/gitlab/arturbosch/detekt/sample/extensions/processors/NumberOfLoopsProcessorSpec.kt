package io.gitlab.arturbosch.detekt.sample.extensions.processors

import dev.detekt.api.DetektVisitor
import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NumberOfLoopsProcessorSpec {

    @Test
    fun `Number of loops sample rule should expect two loops`() {
        val code = """
            fun main() {
                for (i in 0..10) {
                    while (i < 5) {
                        println(i)
                    }
                }
            }
        """.trimIndent()

        val ktFile = compileContentForTest(code)
        ktFile.accept(DetektVisitor())
        NumberOfLoopsProcessor().onProcess(ktFile)

        assertThat(ktFile.getUserData(NumberOfLoopsProcessor.numberOfLoopsKey)).isEqualTo(2)
    }
}

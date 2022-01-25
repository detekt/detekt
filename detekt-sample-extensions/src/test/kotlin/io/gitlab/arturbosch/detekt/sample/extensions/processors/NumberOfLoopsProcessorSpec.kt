package io.gitlab.arturbosch.detekt.sample.extensions.processors

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.resolve.BindingContext
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
        """

        val ktFile = compileContentForTest(code)
        ktFile.accept(DetektVisitor())
        NumberOfLoopsProcessor().onProcess(ktFile, BindingContext.EMPTY)

        assertThat(ktFile.getUserData(NumberOfLoopsProcessor.numberOfLoopsKey)).isEqualTo(2)
    }
}

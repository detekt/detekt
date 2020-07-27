package io.gitlab.arturbosch.detekt.sample.extensions.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.resolve.BindingContext
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class NumberOfLoopsProcessorSpec : Spek({

    describe("Number of Loops sample rule") {

        it("should expect two loops") {
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
})

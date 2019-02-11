package io.gitlab.arturbosch.detekt.sample.extensions.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Artur Bosch
 */
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
            NumberOfLoopsProcessor().onProcess(ktFile)

            assertThat(ktFile.getUserData(NumberOfLoopsProcessor.numberOfLoopsKey)).isEqualTo(2)
        }
    }
})

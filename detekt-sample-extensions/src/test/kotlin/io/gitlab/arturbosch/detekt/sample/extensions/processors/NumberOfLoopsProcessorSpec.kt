package io.gitlab.arturbosch.detekt.sample.extensions.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class NumberOfLoopsProcessorSpec : Spek({

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
})

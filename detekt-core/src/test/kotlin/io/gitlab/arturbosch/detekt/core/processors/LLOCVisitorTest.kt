package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.path
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class LLOCVisitorTest : Spek({

	it("defaultCaseHasOneClassAndAnnotationLine") {
		val file = compileForTest(path.resolve("Default.kt"))

		val lloc = with(file) {
			accept(LLOCVisitor())
			getUserData(logicalLinesKey)
		}

		assertThat(lloc).isEqualTo(2)
	}

	it("llocOfComplexClass") {
		val file = compileForTest(path.resolve("ComplexClass.kt"))

		val lloc = with(file) {
			accept(LLOCVisitor())
			getUserData(logicalLinesKey)
		}

		assertThat(lloc).isEqualTo(85)
	}
})

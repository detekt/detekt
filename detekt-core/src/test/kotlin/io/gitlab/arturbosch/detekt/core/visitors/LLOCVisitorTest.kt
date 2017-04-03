package io.gitlab.arturbosch.detekt.core.visitors

import io.gitlab.arturbosch.detekt.core.path
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
internal class LLOCVisitorTest {

	@Test
	fun defaultCaseHasOneClassAndAnnotationLine() {
		val file = compileForTest(path.resolve("Default.kt"))
		val lloc = LLOCVisitor().visitAndReturn(file)

		assertThat(lloc).isEqualTo(2)
	}

	@Test
	fun llocOfComplexClass() {
		val file = compileForTest(path.resolve("ComplexClass.kt"))
		val lloc = LLOCVisitor().visitAndReturn(file)

		assertThat(lloc).isEqualTo(85)
	}
}
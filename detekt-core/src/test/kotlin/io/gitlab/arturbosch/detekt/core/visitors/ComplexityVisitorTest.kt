package io.gitlab.arturbosch.detekt.core.visitors

import io.gitlab.arturbosch.detekt.api.Context
import io.gitlab.arturbosch.detekt.core.path
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
internal class ComplexityVisitorTest {

	@Test
	fun complexityOfDefaultCaseIsOne() {
		val file = compileForTest(path.resolve("Default.kt"))
		val context = Context()
		val mcc = ComplexityVisitor().visitAndReturn(context, file)

		assertThat(mcc).isEqualTo(0)
	}

	@Test
	fun complexityOfComplexAndNestedClass() {
		val file = compileForTest(path.resolve("ComplexClass.kt"))
		val context = Context()
		val mcc = ComplexityVisitor().visitAndReturn(context, file)

		assertThat(mcc).isEqualTo(42)
	}
}
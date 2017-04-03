package io.gitlab.arturbosch.detekt.core.visitors

import io.gitlab.arturbosch.detekt.core.path
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
internal class ComplexityVisitorTest {

	@Test
	fun complexityOfDefaultCaseIsOne() {
		val file = compileForTest(path.resolve("Default.kt"))
		val mcc = ComplexityVisitor().visitAndReturn(file)

		Assertions.assertThat(mcc).isEqualTo(1)
	}
}
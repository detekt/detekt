package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.path
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
internal class ComplexityVisitorTest {

	@Test
	fun complexityOfDefaultCaseIsOne() {
		val path = path.resolve("Default.kt")

		val mcc = calcComplexity(path)

		assertThat(mcc).isEqualTo(0)
	}

	private fun calcComplexity(path: Path) = with(compileForTest(path)) {
		accept(ComplexityVisitor())
		getUserData(COMPLEXITY_KEY)
	}

	@Test
	fun complexityOfComplexAndNestedClass() {
		val path = path.resolve("ComplexClass.kt")

		val mcc = calcComplexity(path)

		assertThat(mcc).isEqualTo(56)
	}
}

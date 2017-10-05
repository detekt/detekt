package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
class ConditionalPathVisitorTest {

	@Test
	fun pathCount() {
		var counter = 0

		val visitor = ConditionalPathVisitor {
			counter++
		}

		val ktFile = compileForTest(Case.ConditionalPath.path())

		ktFile.accept(visitor)

		assertThat(counter).isEqualTo(5)
	}
}

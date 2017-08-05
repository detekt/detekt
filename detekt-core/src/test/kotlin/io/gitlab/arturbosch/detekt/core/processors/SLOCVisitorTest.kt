package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.path
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class SLOCVisitorTest {

	@Test
	fun defaultClass() {
		val file = compileForTest(path.resolve("Default.kt"))
		val loc = with(file) {
			accept(SLOCVisitor())
			getUserData(SLOC_KEY)
		}
		Assertions.assertThat(loc).isEqualTo(3)
	}
}

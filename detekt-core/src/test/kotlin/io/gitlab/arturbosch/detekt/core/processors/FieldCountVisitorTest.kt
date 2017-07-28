package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.path
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FieldCountVisitorTest {

	@Test
	fun defaultFieldCount() {
		val file = compileForTest(path.resolve("../fields/ClassWithFields.kt"))
		val count = with(file) {
			accept(PropertyCountVisitor())
			getUserData(NUMBER_OF_FIELDS_KEY)
		}
		assertThat(count).isEqualTo(2)
	}
}

package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.path
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.junit.jupiter.api.Test

class MethodCountVisitorTest {

	@Test
	fun defaultMethodCount() {
		val file = compileForTest(path.resolve("ComplexClass.kt"))
		val count = getMethodCount(file)
		assertThat(count).isEqualTo(6)
	}

	private fun getMethodCount(file: KtFile): Int {
		return with(file) {
			accept(FunctionCountVisitor())
			getUserData(NUMBER_OF_FUNCTIONS_KEY)!!
		}
	}
}

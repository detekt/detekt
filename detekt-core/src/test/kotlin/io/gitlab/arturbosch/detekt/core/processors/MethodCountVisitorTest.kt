package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.path
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class MethodCountVisitorTest : Spek({

	it("defaultMethodCount") {
		val file = compileForTest(path.resolve("ComplexClass.kt"))
		val count = getMethodCount(file)
		assertThat(count).isEqualTo(6)
	}

})

private fun getMethodCount(file: KtFile): Int {
	return with(file) {
		accept(FunctionCountVisitor())
		getUserData(numberOfFunctionsKey)!!
	}
}

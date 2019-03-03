package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.path
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MethodCountVisitorTest : Spek({
    describe("Method Count Visitor") {

        it("defaultMethodCount") {
            val file = compileForTest(path.resolve("ComplexClass.kt"))
            val count = getMethodCount(file)
            assertThat(count).isEqualTo(6)
        }
    }
})

private fun getMethodCount(file: KtFile): Int {
    return with(file) {
        accept(FunctionCountVisitor())
        getUserData(numberOfFunctionsKey)!!
    }
}

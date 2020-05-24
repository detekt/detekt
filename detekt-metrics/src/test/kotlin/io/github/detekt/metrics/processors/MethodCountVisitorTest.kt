package io.github.detekt.metrics.processors

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MethodCountVisitorTest : Spek({
    describe("Method Count Visitor") {

        it("defaultMethodCount") {
            val file = compileContentForTest(complexClass)
            val count = getMethodCount(file)
            assertThat(count).isEqualTo(6)
        }
    }
})

private fun getMethodCount(file: KtFile): Int {
    return with(file) {
        accept(FunctionCountVisitor())
        checkNotNull(getUserData(numberOfFunctionsKey))
    }
}

package io.github.detekt.metrics.processors

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ComplexityVisitorTest : Spek({
    describe("something") {

        it("complexityOfDefaultCaseIsOne") {
            val mcc = calcComplexity(default)

            assertThat(mcc).isEqualTo(0)
        }

        it("complexityOfComplexAndNestedClass") {
            val mcc = calcComplexity(complexClass)

            assertThat(mcc).isEqualTo(44)
        }
    }
})

private fun calcComplexity(content: String) =
    with(compileContentForTest(content)) {
        accept(ComplexityVisitor())
        getUserData(complexityKey)
    }

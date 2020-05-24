package io.github.detekt.metrics.processors

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class LLOCVisitorTest : Spek({
    describe("LLOC Visitor") {

        it("defaultCaseHasOneClassAndAnnotationLine") {
            val file = compileContentForTest(default)

            val lloc = with(file) {
                accept(LLOCVisitor())
                getUserData(logicalLinesKey)
            }

            assertThat(lloc).isEqualTo(2)
        }

        it("llocOfComplexClass") {
            val file = compileContentForTest(complexClass)

            val lloc = with(file) {
                accept(LLOCVisitor())
                getUserData(logicalLinesKey)
            }

            assertThat(lloc).isEqualTo(85)
        }
    }
})

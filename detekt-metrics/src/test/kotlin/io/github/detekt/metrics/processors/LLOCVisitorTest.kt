package io.github.detekt.metrics.processors

import io.github.detekt.metrics.path
import io.github.detekt.test.utils.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class LLOCVisitorTest : Spek({
    describe("seomthing") {

        it("defaultCaseHasOneClassAndAnnotationLine") {
            val file = compileForTest(path.resolve("Default.kt"))

            val lloc = with(file) {
                accept(LLOCVisitor())
                getUserData(logicalLinesKey)
            }

            assertThat(lloc).isEqualTo(2)
        }

        it("llocOfComplexClass") {
            val file = compileForTest(path.resolve("ComplexClass.kt"))

            val lloc = with(file) {
                accept(LLOCVisitor())
                getUserData(logicalLinesKey)
            }

            assertThat(lloc).isEqualTo(85)
        }
    }
})

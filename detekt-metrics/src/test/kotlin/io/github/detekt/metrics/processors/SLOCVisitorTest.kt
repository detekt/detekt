package io.github.detekt.metrics.processors

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SLOCVisitorTest : Spek({
    describe("SLOC Visitor") {

        it("defaultClass") {
            val file = compileContentForTest(default)
            val loc = with(file) {
                accept(SLOCVisitor())
                getUserData(sourceLinesKey)
            }
            assertThat(loc).isEqualTo(3)
        }
    }
})

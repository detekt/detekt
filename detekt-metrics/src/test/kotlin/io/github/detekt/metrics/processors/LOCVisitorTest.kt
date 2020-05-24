package io.github.detekt.metrics.processors

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class LOCVisitorTest : Spek({
    describe("LOC Visitor") {

        it("defaultClass") {
            val file = compileContentForTest(default)
            val loc = with(file) {
                accept(LOCVisitor())
                getUserData(linesKey)
            }
            assertThat(loc).isEqualTo(8)
        }
    }
})

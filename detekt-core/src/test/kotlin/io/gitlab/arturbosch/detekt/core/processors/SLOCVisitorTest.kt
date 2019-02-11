package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.path
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SLOCVisitorTest : Spek({
	describe("SLOC Visitor") {

        it("defaultClass") {
            val file = compileForTest(path.resolve("Default.kt"))
            val loc = with(file) {
                accept(SLOCVisitor())
                getUserData(sourceLinesKey)
            }
            Assertions.assertThat(loc).isEqualTo(3)
        }
    }
})

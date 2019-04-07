package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.path
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class LOCVisitorTest : Spek({
    describe("LOC Visitor") {

        it("defaultClass") {
            val file = compileForTest(path.resolve("Default.kt"))
            val loc = with(file) {
                accept(LOCVisitor())
                getUserData(linesKey)
            }
            assertThat(loc).isEqualTo(8)
        }
    }
})

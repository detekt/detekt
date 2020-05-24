package io.github.detekt.metrics.processors

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class CLOCVisitorTest : Spek({
    describe("CLOC") {

        it("commentCases") {
            val file = compileContentForTest(commentsClass)
            val commentLines = with(file) {
                accept(CLOCVisitor())
                getUserData(commentLinesKey)
            }
            assertThat(commentLines).isEqualTo(10)
        }
    }
})

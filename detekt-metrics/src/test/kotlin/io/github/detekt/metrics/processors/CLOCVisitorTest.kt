package io.github.detekt.metrics.processors

import io.github.detekt.metrics.path
import io.github.detekt.test.utils.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class CLOCVisitorTest : Spek({
    describe("CLOC") {

        it("commentCases") {
            val file = compileForTest(path.resolve("CommentsClass.kt"))
            val commentLines = with(file) {
                accept(CLOCVisitor())
                getUserData(commentLinesKey)
            }
            assertThat(commentLines).isEqualTo(10)
        }
    }
})

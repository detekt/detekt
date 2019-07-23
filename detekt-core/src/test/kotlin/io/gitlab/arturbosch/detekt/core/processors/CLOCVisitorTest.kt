package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.path
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class CLOCVisitorTest : Spek({
    describe("CLOC") {

        it("commentCases") {
            val file = compileForTest(path.resolve("../comments/CommentsClass.kt"))
            val commentLines = with(file) {
                accept(CLOCVisitor())
                getUserData(commentLinesKey)
            }
            assertThat(commentLines).isEqualTo(10)
        }
    }
})

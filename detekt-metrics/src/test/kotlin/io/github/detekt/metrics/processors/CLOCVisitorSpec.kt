package io.github.detekt.metrics.processors

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CLOCVisitorSpec {
    @Nested
    inner class `CLOC` {

        @Test
        fun `commentCases`() {
            val file = compileContentForTest(commentsClass)
            val commentLines = with(file) {
                accept(CLOCVisitor())
                getUserData(commentLinesKey)
            }
            assertThat(commentLines).isEqualTo(10)
        }
    }
}

package dev.detekt.metrics.processors

import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CLOCVisitorSpec {

    @Test
    fun commentCases() {
        val file = compileContentForTest(commentsClass)
        val commentLines = with(file) {
            accept(CLOCVisitor())
            getUserData(commentLinesKey)
        }
        assertThat(commentLines).isEqualTo(10)
    }
}

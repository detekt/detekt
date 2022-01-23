package io.github.detekt.metrics.processors

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SLOCVisitorSpec {
    @Nested
    inner class `SLOC Visitor` {

        @Test
        fun `defaultClass`() {
            val file = compileContentForTest(default)
            val loc = with(file) {
                accept(SLOCVisitor())
                getUserData(sourceLinesKey)
            }
            assertThat(loc).isEqualTo(3)
        }
    }
}

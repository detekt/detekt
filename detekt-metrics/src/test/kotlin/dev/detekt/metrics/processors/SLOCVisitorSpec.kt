package dev.detekt.metrics.processors

import dev.detekt.test.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SLOCVisitorSpec {

    @Test
    fun defaultClass() {
        val file = compileContentForTest(default)
        val loc = with(file) {
            accept(SLOCVisitor())
            getUserData(sourceLinesKey)
        }
        assertThat(loc).isEqualTo(3)
    }
}

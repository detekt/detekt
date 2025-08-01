package io.github.detekt.metrics.processors

import dev.detekt.test.utils.compileContentForTest
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

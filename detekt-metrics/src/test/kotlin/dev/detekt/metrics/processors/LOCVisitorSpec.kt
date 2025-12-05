package dev.detekt.metrics.processors

import dev.detekt.test.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LOCVisitorSpec {

    @Test
    fun defaultClass() {
        val file = compileContentForTest(default)
        val loc = with(file) {
            accept(LOCVisitor())
            getUserData(linesKey)
        }
        assertThat(loc).isEqualTo(7)
    }
}

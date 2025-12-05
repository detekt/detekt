package dev.detekt.metrics.processors

import dev.detekt.test.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LLOCVisitorSpec {

    @Test
    fun defaultCaseHasOneClassAndAnnotationLine() {
        val file = compileContentForTest(default)

        val lloc = with(file) {
            accept(LLOCVisitor())
            getUserData(logicalLinesKey)
        }

        assertThat(lloc).isEqualTo(2)
    }

    @Test
    fun llocOfComplexClass() {
        val file = compileContentForTest(complexClass)

        val lloc = with(file) {
            accept(LLOCVisitor())
            getUserData(logicalLinesKey)
        }

        assertThat(lloc).isEqualTo(85)
    }
}

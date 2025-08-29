package dev.detekt.metrics.processors

import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProjectSLOCProcessorSpec {
    @Test
    fun counts() {
        val detektion = ProjectSLOCProcessor().invoke(
            compileContentForTest(default),
            compileContentForTest(emptyEnum),
            compileContentForTest(emptyInterface),
            compileContentForTest(classWithFields),
            compileContentForTest(commentsClass),
            compileContentForTest(complexClass),
        )

        assertThat(detektion.getUserData(sourceLinesKey)).isEqualTo(160)
    }
}

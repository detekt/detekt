package dev.detekt.metrics.processors

import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProjectComplexityProcessorSpec {
    @Test
    fun counts() {
        val detektion = ProjectComplexityProcessor().invoke(
            compileContentForTest(default),
            compileContentForTest(emptyEnum),
            compileContentForTest(emptyInterface),
            compileContentForTest(classWithFields),
            compileContentForTest(commentsClass),
            compileContentForTest(complexClass),
        )

        assertThat(detektion.getUserData(complexityKey)).isEqualTo(45)
    }

}

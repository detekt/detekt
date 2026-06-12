package dev.detekt.metrics.processors

import dev.detekt.test.invoke
import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProjectCyclomaticComplexityProcessorSpec {
    @Test
    fun counts() {
        val detektion = ProjectCyclomaticComplexityProcessor().invoke(
            compileContentForTest(default),
            compileContentForTest(emptyEnum),
            compileContentForTest(emptyInterface),
            compileContentForTest(classWithFields),
            compileContentForTest(commentsClass),
            compileContentForTest(complexClass),
        )

        assertThat(detektion.userData[cyclomaticComplexityKey.toString()]).isEqualTo(45)
    }
}

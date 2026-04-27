package dev.detekt.metrics.processors

import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProjectCognitiveComplexityProcessorSpec {

    @Test
    fun `counts the complexity for the whole file`() {
        val detektion = ProjectCognitiveComplexityProcessor()
            .invoke(compileContentForTest(complexClass))

        assertThat(detektion.userData[cognitiveComplexityKey.toString()]).isEqualTo(50)
    }
}

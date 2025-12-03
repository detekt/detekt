package dev.detekt.metrics.processors

import dev.detekt.metrics.CognitiveComplexity
import dev.detekt.test.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProjectCognitiveComplexityProcessorSpec {

    @Test
    fun `counts the complexity for the whole file`() {
        val detektion = ProjectCognitiveComplexityProcessor()
            .invoke(compileContentForTest(complexClass))

        assertThat(detektion.userData[CognitiveComplexity.KEY.toString()]).isEqualTo(50)
    }
}

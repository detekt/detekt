package io.github.detekt.metrics.processors

import io.github.detekt.metrics.CognitiveComplexity
import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CognitiveComplexityProcessorSpec {

    @Test
    fun `counts the complexity for the whole file`() {
        val file = compileContentForTest(complexClass)

        val value = MetricProcessorTester(file)
            .test(ProjectCognitiveComplexityProcessor(), CognitiveComplexity.KEY)

        assertThat(value).isEqualTo(50)
    }
}

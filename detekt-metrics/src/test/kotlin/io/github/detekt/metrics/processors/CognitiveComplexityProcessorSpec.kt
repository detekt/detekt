package io.github.detekt.metrics.processors

import io.github.detekt.metrics.CognitiveComplexity
import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class CognitiveComplexityProcessorSpec : Spek({

    describe("CognitiveComplexityProcessor") {

        it("counts the complexity for the whole file") {
            val file = compileContentForTest(complexClass)

            val value = MetricProcessorTester(file)
                .test(ProjectCognitiveComplexityProcessor(), CognitiveComplexity.KEY)

            assertThat(value).isEqualTo(46)
        }
    }
})

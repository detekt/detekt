package dev.detekt.report.statistics

import dev.detekt.api.ProjectMetric
import dev.detekt.test.invoke
import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FunctionCountProcessorSpec {
    @Test
    fun counts() {
        val detektion = FunctionCountProcessor().invoke(
            compileContentForTest(default),
            compileContentForTest(emptyEnum),
            compileContentForTest(emptyInterface),
            compileContentForTest(classWithFields),
            compileContentForTest(commentsClass),
            compileContentForTest(complexClass),
        )

        assertThat(detektion.metrics).singleElement()
            .isEqualTo(ProjectMetric("number of functions", 7))
    }

    @Test
    fun defaultMethodCount() {
        val detektion = FunctionCountProcessor().invoke(
            compileContentForTest(complexClass),
        )

        assertThat(detektion.metrics).singleElement()
            .isEqualTo(ProjectMetric("number of functions", 6))
    }
}

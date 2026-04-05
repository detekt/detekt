package dev.detekt.report.statistics

import dev.detekt.api.ProjectMetric
import dev.detekt.test.invoke
import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KtFileCountProcessorSpec {
    @Test
    fun counts() {
        val detektion = KtFileCountProcessor().invoke(
            compileContentForTest(default),
            compileContentForTest(emptyEnum),
            compileContentForTest(emptyInterface),
            compileContentForTest(classWithFields),
            compileContentForTest(commentsClass),
            compileContentForTest(complexClass),
        )

        assertThat(detektion.metrics).singleElement()
            .isEqualTo(ProjectMetric("number of kt files", 6))
    }

    @Test
    fun twoFiles() {
        val detektion = KtFileCountProcessor().invoke(
            compileContentForTest(default),
            compileContentForTest(complexClass),
        )

        assertThat(detektion.metrics).singleElement()
            .isEqualTo(ProjectMetric("number of kt files", 2))
    }
}

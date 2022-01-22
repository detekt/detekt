package io.gitlab.arturbosch.detekt.core.reporting.console

import io.github.detekt.metrics.CognitiveComplexity
import io.github.detekt.metrics.processors.commentLinesKey
import io.github.detekt.metrics.processors.complexityKey
import io.github.detekt.metrics.processors.linesKey
import io.github.detekt.metrics.processors.logicalLinesKey
import io.github.detekt.metrics.processors.sourceLinesKey
import io.github.detekt.test.utils.readResourceContent
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.core.DetektResult
import io.gitlab.arturbosch.detekt.test.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ComplexityReportSpec {

    @Nested
    inner class `complexity report` {

        @Nested
        inner class `several complexity metrics` {

            @Test
            fun `successfully generates a complexity report`() {
                val report = ComplexityReport()
                val expectedContent = readResourceContent("/reporting/complexity-report.txt")
                val detektion = createDetektion()
                addData(detektion)
                assertThat(report.render(detektion)).isEqualTo(expectedContent)
            }

            @Test
            fun `returns null for missing complexity metrics in report`() {
                val report = ComplexityReport()
                val detektion = createDetektion()
                assertThat(report.render(detektion)).isNull()
            }
        }
    }
}

private fun createDetektion(): Detektion = DetektResult(mapOf("Key" to listOf(createFinding())))

private fun addData(detektion: Detektion) {
    detektion.addData(complexityKey, 2)
    detektion.addData(CognitiveComplexity.KEY, 2)
    detektion.addData(linesKey, 10)
    detektion.addData(sourceLinesKey, 6)
    detektion.addData(logicalLinesKey, 5)
    detektion.addData(commentLinesKey, 4)
}

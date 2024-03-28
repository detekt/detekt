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
import io.gitlab.arturbosch.detekt.test.createRuleInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ComplexityReportSpec {

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

private fun createDetektion(): Detektion = DetektResult(listOf(createFinding(createRuleInfo(ruleSetId = "Key"))))

private fun addData(detektion: Detektion) {
    detektion.putUserData(complexityKey, 2)
    detektion.putUserData(CognitiveComplexity.KEY, 2)
    detektion.putUserData(linesKey, 10)
    detektion.putUserData(sourceLinesKey, 6)
    detektion.putUserData(logicalLinesKey, 5)
    detektion.putUserData(commentLinesKey, 4)
}

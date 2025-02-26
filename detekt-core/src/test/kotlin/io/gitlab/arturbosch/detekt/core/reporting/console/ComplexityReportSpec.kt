package io.gitlab.arturbosch.detekt.core.reporting.console

import io.github.detekt.metrics.CognitiveComplexity
import io.github.detekt.metrics.processors.commentLinesKey
import io.github.detekt.metrics.processors.complexityKey
import io.github.detekt.metrics.processors.linesKey
import io.github.detekt.metrics.processors.logicalLinesKey
import io.github.detekt.metrics.processors.sourceLinesKey
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.core.DetektResult
import io.gitlab.arturbosch.detekt.test.createIssue
import io.gitlab.arturbosch.detekt.test.createRuleInstance
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ComplexityReportSpec {

    @Test
    fun `successfully generates a complexity report`() {
        val detektion = createDetektion().apply {
            putUserData(complexityKey, 2)
            putUserData(CognitiveComplexity.KEY, 2)
            putUserData(linesKey, 10)
            putUserData(sourceLinesKey, 6)
            putUserData(logicalLinesKey, 5)
            putUserData(commentLinesKey, 4)
        }
        assertThat(ComplexityReport().render(detektion)).isEqualTo(
            """
                Complexity Report:
                	- 10 lines of code (loc)
                	- 6 source lines of code (sloc)
                	- 5 logical lines of code (lloc)
                	- 4 comment lines of code (cloc)
                	- 2 cyclomatic complexity (mcc)
                	- 2 cognitive complexity
                	- 1 number of total findings
                	- 66% comment source ratio
                	- 400 mcc per 1,000 lloc
                	- 200 findings per 1,000 lloc
                
            """.trimIndent()
        )
    }

    @Test
    fun `returns null for missing complexity metrics in report`() {
        val report = ComplexityReport()
        val detektion = createDetektion()
        assertThat(report.render(detektion)).isNull()
    }
}

private fun createDetektion(): Detektion = DetektResult(
    issues = listOf(createIssue(createRuleInstance(ruleSetId = "Key"))),
    rules = emptyList(),
)

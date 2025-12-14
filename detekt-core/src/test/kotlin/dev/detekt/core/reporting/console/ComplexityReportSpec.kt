package dev.detekt.core.reporting.console

import com.intellij.openapi.util.Key
import dev.detekt.api.testfixtures.TestDetektion
import dev.detekt.api.testfixtures.createIssue
import dev.detekt.api.testfixtures.createRuleInstance
import dev.detekt.metrics.CognitiveComplexity
import dev.detekt.metrics.processors.commentLinesKey
import dev.detekt.metrics.processors.complexityKey
import dev.detekt.metrics.processors.linesKey
import dev.detekt.metrics.processors.logicalLinesKey
import dev.detekt.metrics.processors.sourceLinesKey
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ComplexityReportSpec {

    @Test
    fun `successfully generates a complexity report`() {
        val detektion = createDetektion(
            mapOf(
                complexityKey to 2,
                CognitiveComplexity.KEY to 2,
                linesKey to 10,
                sourceLinesKey to 6,
                logicalLinesKey to 5,
                commentLinesKey to 4,
            )
        )
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

private fun createDetektion(userData: Map<Key<*>, Any> = emptyMap()) =
    TestDetektion(
        createIssue(createRuleInstance(ruleSetId = "Key")),
        userData = userData.mapKeys { (key, _) -> key.toString() },
    )

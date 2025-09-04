package dev.detekt.metrics

import dev.detekt.api.Detektion
import dev.detekt.api.testfixtures.TestDetektion
import dev.detekt.api.testfixtures.createIssue
import dev.detekt.metrics.processors.commentLinesKey
import dev.detekt.metrics.processors.complexityKey
import dev.detekt.metrics.processors.linesKey
import dev.detekt.metrics.processors.logicalLinesKey
import dev.detekt.metrics.processors.sourceLinesKey
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ComplexityReportGeneratorSpec {

    private lateinit var detektion: TestDetektion

    @BeforeEach
    fun setupMocks() {
        detektion = TestDetektion(
            createIssue("test"),
            createIssue("test2", suppressReasons = listOf("suppress")),
            userData = mapOf(
                complexityKey.toString() to 2,
                CognitiveComplexity.KEY.toString() to 2,
                linesKey.toString() to 1000,
                sourceLinesKey.toString() to 6,
                logicalLinesKey.toString() to 5,
                commentLinesKey.toString() to 4,
            )
        )
    }

    @Nested
    inner class `several complexity metrics` {

        @Test
        fun `successfully generates a complexity report`() {
            val expectedContent = listOf(
                "1,000 lines of code (loc)",
                "6 source lines of code (sloc)",
                "5 logical lines of code (lloc)",
                "4 comment lines of code (cloc)",
                "2 cyclomatic complexity (mcc)",
                "2 cognitive complexity",
                "1 number of total findings",
                "66% comment source ratio",
                "400 mcc per 1,000 lloc",
                "200 findings per 1,000 lloc"
            )

            assertThat(generateComplexityReport(detektion)).isEqualTo(expectedContent)
        }
    }

    @Nested
    inner class `several invalid complexity metrics` {

        @Test
        fun `returns null for missing mcc`() {
            detektion.removeData(complexityKey)
            assertThat(generateComplexityReport(detektion)).isNull()
        }

        @Test
        fun `returns null for missing lloc`() {
            detektion.removeData(logicalLinesKey)
            assertThat(generateComplexityReport(detektion)).isNull()

            detektion.userData[logicalLinesKey.toString()] = 0
            assertThat(generateComplexityReport(detektion)).isNull()
        }

        @Test
        fun `returns null for missing sloc`() {
            detektion.removeData(sourceLinesKey)
            assertThat(generateComplexityReport(detektion)).isNull()

            detektion.userData[sourceLinesKey.toString()] = 0
            assertThat(generateComplexityReport(detektion)).isNull()
        }

        @Test
        fun `returns null for missing cloc`() {
            detektion.removeData(complexityKey)
            assertThat(generateComplexityReport(detektion)).isNull()
        }
    }
}

private fun generateComplexityReport(detektion: Detektion): List<String>? {
    val complexityMetric = ComplexityMetric(detektion)
    val generator = ComplexityReportGenerator(complexityMetric)
    return generator.generate()
}

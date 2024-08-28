package io.github.detekt.metrics

import io.github.detekt.metrics.processors.commentLinesKey
import io.github.detekt.metrics.processors.complexityKey
import io.github.detekt.metrics.processors.linesKey
import io.github.detekt.metrics.processors.logicalLinesKey
import io.github.detekt.metrics.processors.sourceLinesKey
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createIssue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ComplexityReportGeneratorSpec {

    private lateinit var detektion: TestDetektion

    @BeforeEach
    fun setupMocks() {
        detektion = TestDetektion(createIssue("test"), createIssue("test2", suppressReasons = listOf("suppress")))
            .withTestData()
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
                "1 number of total code smells",
                "66% comment source ratio",
                "400 mcc per 1,000 lloc",
                "200 code smells per 1,000 lloc"
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

            detektion.putUserData(logicalLinesKey, 0)
            assertThat(generateComplexityReport(detektion)).isNull()
        }

        @Test
        fun `returns null for missing sloc`() {
            detektion.removeData(sourceLinesKey)
            assertThat(generateComplexityReport(detektion)).isNull()

            detektion.putUserData(sourceLinesKey, 0)
            assertThat(generateComplexityReport(detektion)).isNull()
        }

        @Test
        fun `returns null for missing cloc`() {
            detektion.removeData(complexityKey)
            assertThat(generateComplexityReport(detektion)).isNull()
        }
    }
}

private fun TestDetektion.withTestData(): TestDetektion {
    putUserData(complexityKey, 2)
    putUserData(CognitiveComplexity.KEY, 2)
    putUserData(linesKey, 1000)
    putUserData(sourceLinesKey, 6)
    putUserData(logicalLinesKey, 5)
    putUserData(commentLinesKey, 4)
    return this
}

private fun generateComplexityReport(detektion: Detektion): List<String>? {
    val complexityMetric = ComplexityMetric(detektion)
    val generator = ComplexityReportGenerator(complexityMetric)
    return generator.generate()
}

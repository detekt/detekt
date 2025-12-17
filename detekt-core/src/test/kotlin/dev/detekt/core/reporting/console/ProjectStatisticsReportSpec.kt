package dev.detekt.core.reporting.console

import dev.detekt.api.ProjectMetric
import dev.detekt.api.testfixtures.TestDetektion
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProjectStatisticsReportSpec {

    private val subject = ProjectStatisticsReport()

    @Test
    fun `reports the project statistics`() {
        val expected = """
            Project Statistics:
            	- M2: 2
            	- M1: 1

        """.trimIndent()
        val detektion = TestDetektion(
            metrics = listOf(ProjectMetric("M1", 1, priority = 1), ProjectMetric("M2", 2, priority = 2)),
        )
        assertThat(subject.render(detektion)).isEqualTo(expected)
    }

    @Test
    fun `does not report anything for zero metrics`() {
        assertThat(subject.render(TestDetektion())).isNull()
    }
}

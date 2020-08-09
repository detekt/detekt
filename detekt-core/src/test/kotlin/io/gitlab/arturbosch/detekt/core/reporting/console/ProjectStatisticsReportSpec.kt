package io.gitlab.arturbosch.detekt.core.reporting.console

import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.test.TestDetektion
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ProjectStatisticsReportSpec : Spek({

    val subject by memoized { ProjectStatisticsReport() }

    describe("project statistics") {

        it("reports the project statistics") {
            val expected = "Project Statistics:\n\t- M2: 2\n\t- M1: 1\n"
            val detektion = object : TestDetektion() {
                override val metrics: Collection<ProjectMetric> = listOf(
                    ProjectMetric("M1", 1, priority = 1), ProjectMetric("M2", 2, priority = 2))
            }
            assertThat(subject.render(detektion)).isEqualTo(expected)
        }

        it("does not report anything for zero metrics") {
            assertThat(subject.render(TestDetektion())).isNull()
        }
    }
})

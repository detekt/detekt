package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.cli.TestDetektion
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class ProjectStatisticsReportSpec : SubjectSpek<ProjectStatisticsReport>({

	subject { ProjectStatisticsReport() }

	given("several metrics") {

		it("reports the project statistics") {
			val expected = "Project Statistics:\n\t- M2: 2\n\t- M1: 1\n"
			val detektion = object : TestDetektion() {
				override val metrics: Collection<ProjectMetric> = listOf(
						ProjectMetric("M1", 1), ProjectMetric("M2", 2))
			}
			assertThat(subject.render(detektion)).isEqualTo(expected)
		}

		it("does not report anything for zero metrics") {
			assertThat(subject.render(TestDetektion())).isNull()
		}
	}
})

package io.gitlab.arturbosch.detekt.core.reporting.console

import io.github.detekt.test.utils.readResourceContent
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.core.reporting.AutoCorrectableIssueAssert
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class LiteFindingsReportSpec : Spek({

    val subject by memoized { createFindingsReport() }

    describe("findings report") {
        it("reports non-empty findings") {
            assertThat(
                subject
                    .render(
                        TestDetektion(
                            createFinding("SpacingBetweenPackageAndImports"),
                            createFinding("UnnecessarySafeCall")
                        )
                    )
            ).isEqualTo(readResourceContent("/reporting/lite-findings-report.txt"))
        }

        it("reports no findings") {
            val detektion = TestDetektion()
            assertThat(subject.render(detektion)).isNull()
        }

        it("reports no findings with rule set containing no smells") {
            val detektion = object : TestDetektion() {
                override val findings: Map<String, List<Finding>> = mapOf(
                    "Ruleset" to emptyList()
                )
            }
            assertThat(subject.render(detektion)).isNull()
        }

        it("should not add auto corrected issues to report") {
            val report = LiteFindingsReport()
            AutoCorrectableIssueAssert.isReportNull(report)
        }
    }
})

private fun createFindingsReport() = LiteFindingsReport().apply {
    init(Config.empty)
}

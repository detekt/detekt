package io.gitlab.arturbosch.detekt.core.reporting.console

import io.github.detekt.test.utils.readResourceContent
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.core.reporting.AutoCorrectableIssueAssert
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LiteFindingsReportSpec {

    private val subject = createFindingsReport()

    @Test
    fun `reports non-empty findings`() {
        assertThat(
            subject
                .render(
                    TestDetektion(
                        createFinding("SpacingAfterPackageDeclaration"),
                        createFinding("UnnecessarySafeCall")
                    )
                )
        ).isEqualTo(readResourceContent("/reporting/lite-findings-report.txt"))
    }

    @Test
    fun `reports no findings`() {
        val detektion = TestDetektion()
        assertThat(subject.render(detektion)).isNull()
    }

    @Test
    fun `reports no findings with rule set containing no smells`() {
        val detektion = object : TestDetektion() {
            override val findings: Map<RuleSet.Id, List<Finding>> = mapOf(
                RuleSet.Id("Ruleset") to emptyList()
            )
        }
        assertThat(subject.render(detektion)).isNull()
    }

    @Test
    fun `should not add auto corrected issues to report`() {
        val report = LiteFindingsReport()
        AutoCorrectableIssueAssert.isReportNull(report)
    }
}

private fun createFindingsReport() = LiteFindingsReport().apply {
    init(Config.empty)
}

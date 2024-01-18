package io.gitlab.arturbosch.detekt.core.reporting.console

import io.github.detekt.test.utils.readResourceContent
import io.gitlab.arturbosch.detekt.api.Config
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
    fun `should not add auto corrected issues to report`() {
        val report = LiteFindingsReport()
        AutoCorrectableIssueAssert.isReportNull(report)
    }
}

private fun createFindingsReport() = LiteFindingsReport().apply {
    init(Config.empty)
}

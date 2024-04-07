package io.gitlab.arturbosch.detekt.core.reporting.console

import io.github.detekt.test.utils.readResourceContent
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.core.reporting.AutoCorrectableIssueAssert
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createIssue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LiteIssuesReportSpec {

    private val subject = createIssuesReport()

    @Test
    fun `reports non-empty issues`() {
        assertThat(
            subject
                .render(
                    TestDetektion(
                        createIssue("SpacingAfterPackageDeclaration"),
                        createIssue("UnnecessarySafeCall")
                    )
                )
        ).isEqualTo(readResourceContent("/reporting/lite-issues-report.txt"))
    }

    @Test
    fun `reports no issues`() {
        val detektion = TestDetektion()
        assertThat(subject.render(detektion)).isNull()
    }

    @Test
    fun `should not add auto corrected issues to report`() {
        val report = LiteIssuesReport()
        AutoCorrectableIssueAssert.isReportNull(report)
    }
}

private fun createIssuesReport() = LiteIssuesReport().apply {
    init(Config.empty)
}

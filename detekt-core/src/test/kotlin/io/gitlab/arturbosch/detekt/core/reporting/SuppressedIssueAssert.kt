package io.gitlab.arturbosch.detekt.core.reporting

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.TestSetupContext
import io.gitlab.arturbosch.detekt.test.createIssue
import org.assertj.core.api.Assertions.assertThat

internal object SuppressedIssueAssert {

    fun isReportNull(report: ConsoleReport) {
        report.init(TestSetupContext())
        val correctableIssue = createIssue(suppressReasons = listOf("suppressed"))
        val detektionWithCorrectableIssue = TestDetektion(correctableIssue)
        val result = report.render(detektionWithCorrectableIssue)
        assertThat(result).isNull()
    }
}

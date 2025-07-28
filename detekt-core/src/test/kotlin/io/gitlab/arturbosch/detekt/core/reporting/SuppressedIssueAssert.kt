package io.gitlab.arturbosch.detekt.core.reporting

import dev.detekt.api.testfixtures.TestDetektion
import dev.detekt.api.testfixtures.TestSetupContext
import dev.detekt.api.testfixtures.createIssue
import io.gitlab.arturbosch.detekt.api.ConsoleReport
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

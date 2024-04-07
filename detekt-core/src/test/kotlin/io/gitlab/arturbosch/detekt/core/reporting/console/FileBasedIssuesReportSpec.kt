package io.gitlab.arturbosch.detekt.core.reporting.console

import io.github.detekt.test.utils.readResourceContent
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.core.reporting.AutoCorrectableIssueAssert
import io.gitlab.arturbosch.detekt.core.reporting.decolorized
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createEntity
import io.gitlab.arturbosch.detekt.test.createIssue
import io.gitlab.arturbosch.detekt.test.createLocation
import io.gitlab.arturbosch.detekt.test.createRuleInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FileBasedIssuesReportSpec {

    private val subject = createFileBasedIssuesReport()

    @Test
    fun `has the reference content`() {
        val expectedContent = readResourceContent("/reporting/grouped-issues-report.txt")
        val detektion = TestDetektion(
            createIssue(ruleSetId = "Ruleset1", fileName = "File1.kt"),
            createIssue(ruleSetId = "Ruleset1", fileName = "File2.kt"),
            createIssue(ruleSetId = "Ruleset2", fileName = "File1.kt"),
        )

        val output = subject.render(detektion)?.decolorized()

        assertThat(output).isEqualTo(expectedContent)
    }

    @Test
    fun `reports no findings`() {
        val detektion = TestDetektion()
        assertThat(subject.render(detektion)).isNull()
    }

    @Test
    fun `should not add auto corrected issues to report`() {
        val report = FileBasedIssuesReport()
        AutoCorrectableIssueAssert.isReportNull(report)
    }
}

private fun createFileBasedIssuesReport(): FileBasedIssuesReport {
    val report = FileBasedIssuesReport()
    report.init(Config.empty)
    return report
}

private fun createIssue(ruleSetId: String, fileName: String): Issue = createIssue(
    ruleInfo = createRuleInfo(ruleSetId = ruleSetId),
    entity = createEntity(location = createLocation(fileName)),
)

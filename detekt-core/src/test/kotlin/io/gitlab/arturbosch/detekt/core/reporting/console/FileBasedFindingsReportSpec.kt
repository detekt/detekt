package io.gitlab.arturbosch.detekt.core.reporting.console

import io.github.detekt.test.utils.readResourceContent
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding2
import io.gitlab.arturbosch.detekt.core.reporting.AutoCorrectableIssueAssert
import io.gitlab.arturbosch.detekt.core.reporting.decolorized
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createEntity
import io.gitlab.arturbosch.detekt.test.createFinding
import io.gitlab.arturbosch.detekt.test.createLocation
import io.gitlab.arturbosch.detekt.test.createRuleInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FileBasedFindingsReportSpec {

    private val subject = createFileBasedFindingsReport()

    @Test
    fun `has the reference content`() {
        val expectedContent = readResourceContent("/reporting/grouped-findings-report.txt")
        val detektion = TestDetektion(
            createFinding(ruleSetId = "Ruleset1", fileName = "File1.kt"),
            createFinding(ruleSetId = "Ruleset1", fileName = "File2.kt"),
            createFinding(ruleSetId = "Ruleset2", fileName = "File1.kt"),
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
        val report = FileBasedFindingsReport()
        AutoCorrectableIssueAssert.isReportNull(report)
    }
}

private fun createFileBasedFindingsReport(): FileBasedFindingsReport {
    val report = FileBasedFindingsReport()
    report.init(Config.empty)
    return report
}

private fun createFinding(ruleSetId: String, fileName: String): Finding2 = createFinding(
    ruleInfo = createRuleInfo(ruleSetId = ruleSetId),
    entity = createEntity(location = createLocation(fileName)),
)

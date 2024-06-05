package io.gitlab.arturbosch.detekt.core.reporting.console

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.core.reporting.AutoCorrectableIssueAssert
import io.gitlab.arturbosch.detekt.core.reporting.decolorized
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createIssue
import io.gitlab.arturbosch.detekt.test.createLocation
import io.gitlab.arturbosch.detekt.test.createRuleInstance
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FileBasedIssuesReportSpec {

    private val subject = createFileBasedIssuesReport()

    @Test
    fun `has the reference content`() {
        val location1 = createLocation("File1.kt")
        val location2 = createLocation("File2.kt")
        val detektion = TestDetektion(
            createIssue(createRuleInstance(ruleSetId = "Ruleset1"), location1),
            createIssue(createRuleInstance(ruleSetId = "Ruleset1"), location2),
            createIssue(createRuleInstance(ruleSetId = "Ruleset2"), location1),
        )

        val output = subject.render(detektion)?.decolorized()

        assertThat(output).isEqualTo(
            """
                ${location1.path}
                	TestSmell - [TestMessage] at ${location1.compact()}
                	TestSmell - [TestMessage] at ${location1.compact()}
                ${location2.path}
                	TestSmell - [TestMessage] at ${location2.compact()}
                
            """.trimIndent()
        )
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

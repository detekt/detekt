package io.gitlab.arturbosch.detekt.core.reporting.console

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.core.reporting.AutoCorrectableIssueAssert
import io.gitlab.arturbosch.detekt.core.reporting.decolorized
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createIssue
import io.gitlab.arturbosch.detekt.test.createRuleInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class IssuesReportSpec {

    private val subject = createIssuesReport()

    @Test
    fun `has the reference content`() {
        val detektion = TestDetektion(
            createIssue(createRuleInfo(ruleSetId = "Ruleset1")),
            createIssue(createRuleInfo(ruleSetId = "Ruleset1")),
            createIssue(createRuleInfo(ruleSetId = "Ruleset2")),
        )

        val output = subject.render(detektion)?.decolorized()

        assertThat(output).isEqualTo(
            """
                Ruleset1
                	TestSmell - [TestMessage] at TestFile.kt:1:1
                	TestSmell - [TestMessage] at TestFile.kt:1:1
                Ruleset2
                	TestSmell - [TestMessage] at TestFile.kt:1:1
                
            """.trimIndent()
        )
    }

    @Test
    fun `reports no issues`() {
        val detektion = TestDetektion()
        assertThat(subject.render(detektion)).isNull()
    }

    @Test
    fun `should not add auto corrected issues to report`() {
        val report = IssuesReport()
        AutoCorrectableIssueAssert.isReportNull(report)
    }

    @Test
    fun `truncates long message`() {
        val expectedContent = """
            Ruleset
            	LongRule - [This is just a long message that should be truncated after a given threshold is (...)] at TestFile.kt:1:1
            	MultilineRule - [A multiline message.] at TestFile.kt:1:1
            
        """.trimIndent()
        val longMessage = "This is just a long message that should be truncated after a given " +
            "threshold is reached."
        val multilineMessage = "A multiline\n\r\tmessage.\t "
        val detektion = TestDetektion(
            createIssue(createRuleInfo("LongRule", "Ruleset"), message = longMessage),
            createIssue(createRuleInfo("MultilineRule", "Ruleset"), message = multilineMessage),
        )
        assertThat(subject.render(detektion)?.decolorized()).isEqualTo(expectedContent)
    }
}

private fun createIssuesReport(): IssuesReport {
    val report = IssuesReport()
    report.init(Config.empty)
    return report
}

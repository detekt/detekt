package io.gitlab.arturbosch.detekt.core.reporting.console

import io.github.detekt.test.utils.readResourceContent
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.core.reporting.AutoCorrectableIssueAssert
import io.gitlab.arturbosch.detekt.core.reporting.decolorized
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createFinding
import io.gitlab.arturbosch.detekt.test.createRuleInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FindingsReportSpec {

    private val subject = createFindingsReport()

    @Test
    fun `has the reference content`() {
        val expectedContent = readResourceContent("/reporting/findings-report.txt")
        val detektion = TestDetektion(
            createFinding(createRuleInfo(ruleSetId = "Ruleset1")),
            createFinding(createRuleInfo(ruleSetId = "Ruleset1")),
            createFinding(createRuleInfo(ruleSetId = "Ruleset2")),
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
        val report = FindingsReport()
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
            createFinding(createRuleInfo("LongRule", "Ruleset"), message = longMessage),
            createFinding(createRuleInfo("MultilineRule", "Ruleset"), message = multilineMessage),
        )
        assertThat(subject.render(detektion)?.decolorized()).isEqualTo(expectedContent)
    }
}

private fun createFindingsReport(): FindingsReport {
    val report = FindingsReport()
    report.init(Config.empty)
    return report
}

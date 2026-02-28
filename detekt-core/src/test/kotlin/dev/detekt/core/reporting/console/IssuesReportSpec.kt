package dev.detekt.core.reporting.console

import dev.detekt.api.Severity.Error
import dev.detekt.api.Severity.Info
import dev.detekt.api.Severity.Warning
import dev.detekt.api.testfixtures.TestDetektion
import dev.detekt.api.testfixtures.TestSetupContext
import dev.detekt.api.testfixtures.createIssue
import dev.detekt.api.testfixtures.createIssueLocation
import dev.detekt.api.testfixtures.createRuleInstance
import dev.detekt.core.reporting.SuppressedIssueAssert
import dev.detekt.core.reporting.decolorized
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.absolute

class IssuesReportSpec {
    private val basePath = Path("").absolute()
    private val subject = IssuesReport().apply { init(TestSetupContext(basePath = basePath)) }

    @Test
    fun `has the reference content`() {
        val location = createIssueLocation()
        val detektion = TestDetektion(
            createIssue(createRuleInstance(ruleSetId = "Ruleset1"), location, severity = Error),
            createIssue(createRuleInstance(ruleSetId = "Ruleset1"), location, severity = Warning),
            createIssue(createRuleInstance(ruleSetId = "Ruleset2"), location, severity = Info),
        )

        val output = subject.render(detektion)?.decolorized()

        assertThat(output).isEqualTo(
            """
                Ruleset1
                	e: TestSmell/id - [TestMessage] at ${basePath.resolve(location.path)}:1:1
                	w: TestSmell/id - [TestMessage] at ${basePath.resolve(location.path)}:1:1
                Ruleset2
                	i: TestSmell/id - [TestMessage] at ${basePath.resolve(location.path)}:1:1
                
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
        SuppressedIssueAssert.isReportNull(report)
    }

    @Test
    fun `truncates long message`() {
        val detektion = TestDetektion(
            createIssue(
                createRuleInstance("LongRule", "Ruleset"),
                message = "This is just a long message that should be truncated after a given threshold is reached.",
            ),
            createIssue(
                createRuleInstance("MultilineRule", "Ruleset"),
                message = "A multiline\n\r\tmessage.\t ",
            ),
        )
        val output = subject.render(detektion)?.decolorized()
        assertThat(output)
            .contains(
                "LongRule - [This is just a long message that should be truncated after a given threshold is (...)]"
            )
        assertThat(output)
            .contains("MultilineRule - [A multiline message.]")
    }
}

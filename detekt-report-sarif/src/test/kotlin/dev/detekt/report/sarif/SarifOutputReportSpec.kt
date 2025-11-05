package dev.detekt.report.sarif

import dev.detekt.api.RuleInstance
import dev.detekt.api.RuleSetId
import dev.detekt.api.Severity
import dev.detekt.api.testfixtures.TestDetektion
import dev.detekt.api.testfixtures.TestSetupContext
import dev.detekt.api.testfixtures.createIssue
import dev.detekt.api.testfixtures.createIssueEntity
import dev.detekt.api.testfixtures.createIssueLocation
import dev.detekt.test.utils.readResourceContent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI

class SarifOutputReportSpec {

    @Test
    fun `renders multiple issues`() {
        val rules = createRuleInstances()
        val result = TestDetektion(
            createIssue(
                ruleInstance = rules[0],
                entity = createIssueEntity(
                    signature = "one",
                    location = createIssueLocation(position = 1 to 1, endPosition = 2 to 3),
                ),
                severity = Severity.Error
            ),
            createIssue(
                ruleInstance = rules[4],
                entity = createIssueEntity(
                    signature = "two",
                    location = createIssueLocation(position = 1 to 1, endPosition = 2 to 3),
                ),
                severity = Severity.Info,
                suppressReasons = listOf("suppress")
            ),
            createIssue(
                ruleInstance = rules[1],
                entity = createIssueEntity(
                    signature = "three",
                    location = createIssueLocation(position = 3 to 5, endPosition = 3 to 5),
                ),
                severity = Severity.Warning
            ),
            createIssue(
                ruleInstance = rules[3],
                entity = createIssueEntity(
                    signature = "four",
                    location = createIssueLocation(position = 2 to 1, endPosition = 3 to 1),
                ),
                severity = Severity.Info
            ),
            rules = createRuleInstances(),
        )

        val report = SarifOutputReport()
            .apply { init(TestSetupContext()) }
            .render(result)

        val expectedReport = readResourceContent("vanilla.sarif.json")

        assertThat(report).isEqualToIgnoringWhitespace(expectedReport)
    }
}

private fun createRuleInstances() = listOf(
    RuleInstance(
        id = "TestSmellA",
        ruleSetId = RuleSetId("RuleSet1"),
        url = URI("http://example.org/TestSmellA"),
        description = "Description A",
        severity = Severity.Error,
        active = true,
    ),
    RuleInstance(
        id = "TestSmellB",
        ruleSetId = RuleSetId("RuleSet2"),
        url = URI("http://example.org/TestSmellB"),
        description = "Description B",
        severity = Severity.Warning,
        active = true,
    ),
    RuleInstance(
        id = "TestSmellC",
        ruleSetId = RuleSetId("RuleSet2"),
        url = URI("http://example.org/TestSmellC"),
        description = "Description C",
        severity = Severity.Info,
        active = false,
    ),
    RuleInstance(
        id = "TestSmellC/id",
        ruleSetId = RuleSetId("RuleSet2"),
        url = URI("http://example.org/TestSmellC"),
        description = "Description C",
        severity = Severity.Error,
        active = false,
    ),
    RuleInstance(
        id = "TestSmellD",
        ruleSetId = RuleSetId("RuleSet2"),
        url = null,
        description = "Description D",
        severity = Severity.Error,
        active = false,
    ),
)

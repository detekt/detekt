package io.github.detekt.report.sarif

import io.github.detekt.test.utils.readResourceContent
import io.gitlab.arturbosch.detekt.api.RuleInstance
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.TestSetupContext
import io.gitlab.arturbosch.detekt.test.createEntity
import io.gitlab.arturbosch.detekt.test.createIssue
import io.gitlab.arturbosch.detekt.test.createLocation
import io.gitlab.arturbosch.detekt.test.createRuleInstance
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI

class SarifOutputReportSpec {

    @Test
    fun `renders multiple issues`() {
        val result = TestDetektion(
            createIssue(
                ruleInstance = createRuleInstance("TestSmellA/id", "RuleSet1"),
                entity = createEntity(
                    signature = "one",
                    location = createLocation(position = 1 to 1, endPosition = 2 to 3),
                ),
                severity = Severity.Error
            ),
            createIssue(
                ruleInstance = createRuleInstance("TestSmellD/id", "RuleSet1"),
                entity = createEntity(
                    signature = "two",
                    location = createLocation(position = 1 to 1, endPosition = 2 to 3),
                ),
                severity = Severity.Error,
                suppressReasons = listOf("suppress")
            ),
            createIssue(
                ruleInstance = createRuleInstance("TestSmellB/id", "RuleSet2"),
                entity = createEntity(
                    signature = "three",
                    location = createLocation(position = 3 to 5, endPosition = 3 to 5),
                ),
                severity = Severity.Warning
            ),
            createIssue(
                ruleInstance = createRuleInstance("TestSmellC/id", "RuleSet2"),
                entity = createEntity(
                    signature = "four",
                    location = createLocation(position = 2 to 1, endPosition = 3 to 1),
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
        ruleSetId = RuleSet.Id("RuleSet1"),
        url = URI("http://example.org/TestSmellA"),
        description = "Description A",
        severity = Severity.Error,
        active = true,
    ),
    RuleInstance(
        id = "TestSmellB",
        ruleSetId = RuleSet.Id("RuleSet2"),
        url = URI("http://example.org/TestSmellB"),
        description = "Description B",
        severity = Severity.Warning,
        active = true,
    ),
    RuleInstance(
        id = "TestSmellC",
        ruleSetId = RuleSet.Id("RuleSet2"),
        url = URI("http://example.org/TestSmellC"),
        description = "Description C",
        severity = Severity.Info,
        active = false,
    ),
    RuleInstance(
        id = "TestSmellD",
        ruleSetId = RuleSet.Id("RuleSet2"),
        url = null,
        description = "Description D",
        severity = Severity.Error,
        active = false,
    ),
)

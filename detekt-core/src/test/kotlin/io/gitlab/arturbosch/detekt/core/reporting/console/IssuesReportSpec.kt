package io.gitlab.arturbosch.detekt.core.reporting.console

import io.gitlab.arturbosch.detekt.core.reporting.SuppressedIssueAssert
import io.gitlab.arturbosch.detekt.core.reporting.decolorized
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.TestSetupContext
import io.gitlab.arturbosch.detekt.test.createIssue
import io.gitlab.arturbosch.detekt.test.createLocation
import io.gitlab.arturbosch.detekt.test.createRuleInstance
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.absolute

class IssuesReportSpec {
    private val basePath = Path("").absolute()
    private val subject = IssuesReport().apply { init(TestSetupContext(basePath = basePath)) }

    @Test
    fun `has the reference content`() {
        val location = createLocation()
        val detektion = TestDetektion(
            createIssue(createRuleInstance(ruleSetId = "Ruleset1"), location),
            createIssue(createRuleInstance(ruleSetId = "Ruleset1"), location),
            createIssue(createRuleInstance(ruleSetId = "Ruleset2"), location),
        )

        val output = subject.render(detektion)?.decolorized()

        assertThat(output).isEqualTo(
            """
                Ruleset1
                	TestSmell/id - [TestMessage] at ${basePath.resolve(location.path)}:1:1
                	TestSmell/id - [TestMessage] at ${basePath.resolve(location.path)}:1:1
                Ruleset2
                	TestSmell/id - [TestMessage] at ${basePath.resolve(location.path)}:1:1
                
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

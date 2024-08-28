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

class FileBasedIssuesReportSpec {
    private val basePath = Path("").absolute()
    private val subject = FileBasedIssuesReport().apply { init(TestSetupContext(basePath = basePath)) }

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
                ${basePath.resolve(location1.path)}
                	TestSmell/id - [TestMessage] at ${basePath.resolve(location1.path)}:1:1
                	TestSmell/id - [TestMessage] at ${basePath.resolve(location1.path)}:1:1
                ${basePath.resolve(location2.path)}
                	TestSmell/id - [TestMessage] at ${basePath.resolve(location2.path)}:1:1
                
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
        SuppressedIssueAssert.isReportNull(report)
    }
}

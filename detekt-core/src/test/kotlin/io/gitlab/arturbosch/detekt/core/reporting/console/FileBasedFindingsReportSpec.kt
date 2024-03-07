package io.gitlab.arturbosch.detekt.core.reporting.console

import io.github.detekt.test.utils.readResourceContent
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding2
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.core.reporting.AutoCorrectableIssueAssert
import io.gitlab.arturbosch.detekt.core.reporting.decolorized
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createEntity
import io.gitlab.arturbosch.detekt.test.createFinding
import io.gitlab.arturbosch.detekt.test.createLocation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FileBasedFindingsReportSpec {

    private val subject = createFileBasedFindingsReport()

    @Test
    fun `has the reference content`() {
        val expectedContent = readResourceContent("/reporting/grouped-findings-report.txt")
        val detektion = object : TestDetektion() {
            override val findings: Map<RuleSet.Id, List<Finding2>> = mapOf(
                RuleSet.Id("Ruleset1") to listOf(
                    createFinding(fileName = "File1.kt"),
                    createFinding(fileName = "File2.kt")
                ),
                RuleSet.Id("EmptyRuleset") to emptyList(),
                RuleSet.Id("Ruleset2") to listOf(createFinding(fileName = "File1.kt"))
            )
        }

        val output = subject.render(detektion)?.decolorized()

        assertThat(output).isEqualTo(expectedContent)
    }

    @Test
    fun `reports no findings`() {
        val detektion = TestDetektion()
        assertThat(subject.render(detektion)).isNull()
    }

    @Test
    fun `reports no findings when no rule set contains smells`() {
        val detektion = object : TestDetektion() {
            override val findings: Map<RuleSet.Id, List<Finding2>> = mapOf(
                RuleSet.Id("EmptySmells") to emptyList()
            )
        }
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

private fun createFinding(fileName: String): Finding2 = createFinding(
    entity = createEntity(location = createLocation(fileName)),
)

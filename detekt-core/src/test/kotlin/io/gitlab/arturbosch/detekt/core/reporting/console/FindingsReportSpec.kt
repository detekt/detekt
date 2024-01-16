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
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FindingsReportSpec {

    private val subject = createFindingsReport()

    @Test
    fun `has the reference content`() {
        val expectedContent = readResourceContent("/reporting/findings-report.txt")
        val detektion = object : TestDetektion() {
            override val findings: Map<RuleSet.Id, List<Finding2>> = mapOf(
                RuleSet.Id("Ruleset1") to listOf(createFinding(), createFinding()),
                RuleSet.Id("EmptyRuleset") to emptyList(),
                RuleSet.Id("Ruleset2") to listOf(createFinding())
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
    fun `reports no findings with rule set containing no smells`() {
        val detektion = object : TestDetektion() {
            override val findings: Map<RuleSet.Id, List<Finding2>> = mapOf(
                RuleSet.Id("Ruleset") to emptyList()
            )
        }
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
            	LongRule - [This is just a long message that should be truncated after a given threshold is (...)] at File.kt:1:1
            	MultilineRule - [A multiline message.] at File.kt:1:1
            
        """.trimIndent()
        val longMessage = "This is just a long message that should be truncated after a given " +
            "threshold is reached."
        val multilineMessage = "A multiline\n\r\tmessage.\t "
        val detektion = object : TestDetektion() {
            override val findings: Map<RuleSet.Id, List<Finding2>> = mapOf(
                RuleSet.Id("Ruleset") to listOf(
                    createFinding("LongRule", createEntity(), longMessage),
                    createFinding("MultilineRule", createEntity(), multilineMessage),
                ),
            )
        }
        assertThat(subject.render(detektion)?.decolorized()).isEqualTo(expectedContent)
    }
}

private fun createFindingsReport(): FindingsReport {
    val report = FindingsReport()
    report.init(Config.empty)
    return report
}

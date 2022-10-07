package io.gitlab.arturbosch.detekt.core.reporting.console

import io.github.detekt.test.utils.readResourceContent
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.core.reporting.AutoCorrectableIssueAssert
import io.gitlab.arturbosch.detekt.core.reporting.decolorized
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createEntity
import io.gitlab.arturbosch.detekt.test.createFinding
import io.gitlab.arturbosch.detekt.test.createIssue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FindingsReportSpec {

    private val subject = createFindingsReport()

    @Nested
    inner class `reports the debt per rule set and the overall debt` {
        private val expectedContent = readResourceContent("/reporting/findings-report.txt")
        val detektion = object : TestDetektion() {
            override val findings: Map<String, List<Finding>> = mapOf(
                "Ruleset1" to listOf(createFinding(), createFinding()),
                "EmptyRuleset" to emptyList(),
                "Ruleset2" to listOf(createFinding())
            )
        }

        var output: String? = null

        @BeforeEach
        fun setUp() {
            output = subject.render(detektion)?.decolorized()
        }

        @Test
        fun `has the reference content`() {
            assertThat(output).isEqualTo(expectedContent)
        }

        @Test
        fun `does contain the rule set id of rule sets with findings`() {
            assertThat(output).contains("TestSmell")
        }
    }

    @Test
    fun `reports no findings`() {
        val detektion = TestDetektion()
        assertThat(subject.render(detektion)).isNull()
    }

    @Test
    fun `reports no findings with rule set containing no smells`() {
        val detektion = object : TestDetektion() {
            override val findings: Map<String, List<Finding>> = mapOf(
                "Ruleset" to emptyList()
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
        val expectedContent = readResourceContent("/reporting/long-messages-report.txt")
        val longMessage = "This is just a long message that should be truncated after a given " +
            "threshold is reached."
        val multilineMessage = "A multiline\n\r\tmessage.\t "
        val detektion = object : TestDetektion() {
            override val findings: Map<String, List<Finding>> = mapOf(
                "Ruleset" to listOf(
                    createFinding(createIssue("LongRule"), createEntity("File.kt"), longMessage),
                    createFinding(createIssue("MultilineRule"), createEntity("File.kt"), multilineMessage)
                )
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

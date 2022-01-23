package io.gitlab.arturbosch.detekt.core.reporting.console

import io.github.detekt.test.utils.readResourceContent
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.core.reporting.AutoCorrectableIssueAssert
import io.gitlab.arturbosch.detekt.core.reporting.decolorized
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FileBasedFindingsReportSpec {

    private val subject = createFileBasedFindingsReport()

    @Nested
    inner class `findings report` {

        @Nested
        inner class `reports the debt per file and rule set with the overall debt` {

            @Test
            fun `has the reference content`() {
                val expectedContent = readResourceContent("/reporting/grouped-findings-report.txt")
                val detektion = object : TestDetektion() {
                    override val findings: Map<String, List<Finding>> = mapOf(
                        "Ruleset1" to listOf(
                            createFinding(fileName = "File1.kt"),
                            createFinding(fileName = "File2.kt")
                        ),
                        "EmptyRuleset" to emptyList(),
                        "Ruleset2" to listOf(createFinding(fileName = "File1.kt"))
                    )
                }

                val output = subject.render(detektion)?.decolorized()

                assertThat(output).isEqualTo(expectedContent)
            }
        }

        @Test
        fun `reports no findings`() {
            val detektion = TestDetektion()
            assertThat(subject.render(detektion)).isNull()
        }

        @Test
        fun `reports no findings when no rule set contains smells`() {
            val detektion = object : TestDetektion() {
                override val findings: Map<String, List<Finding>> = mapOf(
                    "EmptySmells" to emptyList()
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
}

private fun createFileBasedFindingsReport(): FileBasedFindingsReport {
    val report = FileBasedFindingsReport()
    report.init(Config.empty)
    return report
}

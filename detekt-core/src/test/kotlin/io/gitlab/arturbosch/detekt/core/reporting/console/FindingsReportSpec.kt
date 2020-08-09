package io.gitlab.arturbosch.detekt.core.reporting.console

import io.github.detekt.test.utils.readResourceContent
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.core.reporting.AutoCorrectableIssueAssert
import io.gitlab.arturbosch.detekt.core.reporting.decolorized
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class FindingsReportSpec : Spek({

    val subject by memoized { createFindingsReport() }

    describe("findings report") {

        context("reports the debt per rule set and the overall debt") {
            val expectedContent by memoized { readResourceContent("/reporting/findings-report.txt") }
            val detektion by memoized {
                object : TestDetektion() {
                    override val findings: Map<String, List<Finding>> = mapOf(
                        Pair("Ruleset1", listOf(createFinding(), createFinding())),
                        Pair("EmptyRuleset", emptyList()),
                        Pair("Ruleset2", listOf(createFinding()))
                    )
                }
            }

            var output: String? = null

            beforeEachTest {
                output = subject.render(detektion)?.decolorized()
            }

            it("has the reference content") {
                assertThat(output).isEqualTo(expectedContent)
            }

            it("does contain the rule set id of rule sets with findings") {
                assertThat(output).contains("TestSmell")
            }
        }

        it("reports no findings") {
            val detektion = TestDetektion()
            assertThat(subject.render(detektion)).isNull()
        }

        it("reports no findings with rule set containing no smells") {
            val detektion = object : TestDetektion() {
                override val findings: Map<String, List<Finding>> = mapOf(
                    Pair("Ruleset", emptyList()))
            }
            assertThat(subject.render(detektion)).isNull()
        }

        it("should not add auto corrected issues to report") {
            val report = FindingsReport()
            AutoCorrectableIssueAssert.isReportNull(report)
        }
    }
})

private fun createFindingsReport(): FindingsReport {
    val report = FindingsReport()
    report.init(Config.empty)
    return report
}

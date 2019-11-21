package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.cli.createCorrectableFinding
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.cli.createFinding
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class FileBasedFindingsReportSpec : Spek({

    val subject by memoized { createFileBasedFindingsReport() }

    describe("findings report") {

        context("reports the debt per file and rule set with the overall debt") {
            val expectedContent = readResource("grouped-findings-report.txt")
            val detektion = object : TestDetektion() {
                override val findings: Map<String, List<Finding>> = mapOf(
                    Pair(
                        "TestSmell",
                        listOf(
                            createFinding(ruleName = "NewRule", fileName = "AnotherFile.kt"),
                            createFinding(ruleName = "NewRule", fileName = "TestFile.kt"),
                            createFinding(ruleName = "RandomRule", fileName = "AnotherFile.kt")
                        )
                    ),
                    Pair(
                        "AnotherTestSmell",
                        listOf(
                            createFinding(ruleName = "AnotherRule", fileName = "AnotherFile.kt"),
                            createFinding(ruleName = "AnotherRandomRule", fileName = "TestFile.kt")
                        )
                    ),
                    Pair("EmptySmells", emptyList())
                )
            }

            it("has the reference content") {
                val output = subject.render(detektion)?.trimEnd()?.decolorized()
                assertThat(output).isEqualTo(expectedContent)
            }
        }

        it("reports no findings") {
            val detektion = TestDetektion()
            assertThat(subject.render(detektion)).isNull()
        }

        it("reports no findings when no rule set contains smells") {
            val detektion = object : TestDetektion() {
                override val findings: Map<String, List<Finding>> = mapOf(
                    Pair("EmptySmells", emptyList())
                )
            }
            assertThat(subject.render(detektion)).isNull()
        }

        it("should not add auto corrected issues to report") {
            val report = FileBasedFindingsReport()
            report.init(TestConfig("excludeCorrectable" to "true"))
            val correctableCodeSmell = createCorrectableFinding()
            val detektionWithCorrectableSmell = TestDetektion(correctableCodeSmell)
            assertThat(report.render(detektionWithCorrectableSmell)).isNull()
        }
    }
})

private fun createFileBasedFindingsReport(): FileBasedFindingsReport {
    val report = FileBasedFindingsReport()
    report.init(Config.empty)
    return report
}

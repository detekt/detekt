package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.cli.TestDetektion
import io.gitlab.arturbosch.detekt.cli.console.readResource
import io.gitlab.arturbosch.detekt.cli.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class FindBugsXmlOutputReportTest : Spek({

    val outputFormat = FindBugsXmlOutputReport()

    fun FindBugsXmlOutputReport.renderNormalized(detektion: Detektion) = render(detektion)
        .replace(Regex("<BugCollection[^>]+>"), "<BugCollection>")
        .trimEnd()

    describe("findbugs xml report") {
        context("several detekt findings") {
            val expectedContent = readResource("findbugs-report.xml")
            val detektion = object : TestDetektion() {
                override val findings: Map<String, List<Finding>> = mapOf(
                    Pair("TestSmell", listOf(createFinding(), createFinding())),
                    Pair("EmptySmells", emptyList())
                )
            }
            val output = outputFormat.renderNormalized(detektion)
            assertThat(output).isEqualTo(expectedContent)
        }

        it("reports no findings") {
            val expectedContent = readResource("findbugs-report-empty.xml")
            val detektion = TestDetektion()
            val output = outputFormat.renderNormalized(detektion)
            assertThat(output).isEqualTo(expectedContent)
        }
    }

})

package io.github.detekt.report.sarif

import io.github.detekt.test.utils.readResourceContent
import io.github.detekt.tooling.api.VersionProvider
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.SeverityLevel
import io.gitlab.arturbosch.detekt.api.internal.whichOS
import io.gitlab.arturbosch.detekt.test.EmptySetupContext
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createEntity
import io.gitlab.arturbosch.detekt.test.createIssue
import io.gitlab.arturbosch.detekt.test.createFindingFromRelativePath
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SarifOutputReportSpec : Spek({

    describe("sarif output report") {

        it("renders multiple issues") {
            val result = TestDetektion(
                createFinding(ruleName = "TestSmellA", severity = SeverityLevel.ERROR),
                createFinding(ruleName = "TestSmellB", severity = SeverityLevel.WARNING),
                createFinding(ruleName = "TestSmellC", severity = SeverityLevel.INFO)
            )

            val report = SarifOutputReport().apply { init(EmptySetupContext()) }
                .render(result)
                .stripWhitespace()

            assertThat(report).isEqualTo(
                readResourceContent("vanilla.sarif.json").stripWhitespace())
        }

        it("renders multiple issues with relative path") {
            val result = TestDetektion(
                createFindingFromRelativePath(ruleName = "TestSmellA"),
                createFindingFromRelativePath(ruleName = "TestSmellB"),
                createFindingFromRelativePath(ruleName = "TestSmellC")
            )

            val report = SarifOutputReport().apply { init(EmptySetupContext()) }
                .render(result)
                .stripWhitespace()

            val expectedReport = readResourceContent("relative_path.sarif.json")
                .stripWhitespace()

            // Note: On Github actions, windows file URI is on D: drive
            val systemAwareExpectedReport = if (whichOS().startsWith("windows", ignoreCase = true)) {
                expectedReport.replace("file:/", "file:/D:/")
            } else {
                expectedReport
            }
            assertThat(report).isEqualTo(systemAwareExpectedReport)
        }
    }
})

private fun createFinding(ruleName: String, severity: SeverityLevel): Finding {
    return object : CodeSmell(createIssue(ruleName), createEntity("TestFile.kt"), "TestMessage") {
        override val severity: SeverityLevel
            get() = severity
    }
}

internal fun String.stripWhitespace() = replace(Regex("\\s"), "")

internal class TestVersionProvider : VersionProvider {

    override fun current(): String = "1.0.0"
}

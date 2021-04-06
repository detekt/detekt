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
import io.gitlab.arturbosch.detekt.test.createFindingForRelativePath
import io.gitlab.arturbosch.detekt.test.createIssue
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

class SarifOutputReportSpec : Spek({

    describe("sarif output report") {

        it("renders multiple issues") {
            val result = TestDetektion(
                createFinding(ruleName = "TestSmellA", severity = SeverityLevel.ERROR),
                createFinding(ruleName = "TestSmellB", severity = SeverityLevel.WARNING),
                createFinding(ruleName = "TestSmellC", severity = SeverityLevel.INFO)
            )

            val report = SarifOutputReport()
                .apply { init(EmptySetupContext()) }
                .render(result)

            assertThat(report).isEqualToIgnoringWhitespace(readResourceContent("vanilla.sarif.json"))
        }

        it("renders multiple issues with relative path") {
            val basePath = "/Users/tester/detekt/"
            val result = TestDetektion(
                createFindingForRelativePath(ruleName = "TestSmellA", basePath = basePath),
                createFindingForRelativePath(ruleName = "TestSmellB", basePath = basePath),
                createFindingForRelativePath(ruleName = "TestSmellC", basePath = basePath)
            )

            val report = SarifOutputReport()
                .apply {
                    init(
                        EmptySetupContext().apply {
                            register(DETEKT_OUTPUT_REPORT_BASE_PATH_KEY, Paths.get(basePath))
                        }
                    )
                }
                .render(result)
                .stripWhitespace()

            val expectedReport = readResourceContent("relative_path.sarif.json")

            // Note: Github CI uses D: drive, but it could be any drive for local development
            val systemAwareExpectedReport = if (whichOS().startsWith("windows", ignoreCase = true)) {
                val winRoot = Paths.get("/").toAbsolutePath().toString().replace("\\", "/")
                expectedReport.replace("file:///", "file://$winRoot")
            } else {
                expectedReport
            }

            assertThat(report).isEqualToIgnoringWhitespace(systemAwareExpectedReport)
        }
    }
})

private fun createFinding(ruleName: String, severity: SeverityLevel): Finding {
    return object : CodeSmell(createIssue(ruleName), createEntity("TestFile.kt"), "TestMessage") {
        override val severity: SeverityLevel
            get() = severity
    }
}

private fun String.stripWhitespace() = replace(Regex("\\s"), "")

internal class TestVersionProvider : VersionProvider {

    override fun current(): String = "1.0.0"
}

package dev.detekt.core.reporting

import dev.detekt.api.Detektion
import dev.detekt.api.Notification
import dev.detekt.api.Notification.Level
import dev.detekt.api.OutputReport
import dev.detekt.api.testfixtures.createIssue
import dev.detekt.api.testfixtures.createIssueEntity
import dev.detekt.api.testfixtures.createIssueLocation
import dev.detekt.api.testfixtures.createRuleInstance
import dev.detekt.core.createNullLoggingSpec
import dev.detekt.core.createProcessingSettings
import dev.detekt.core.tooling.withSettings
import dev.detekt.report.html.HtmlOutputReport
import dev.detekt.report.markdown.MarkdownOutputReport
import dev.detekt.report.sarif.SarifOutputReport
import dev.detekt.report.xml.CheckstyleOutputReport
import dev.detekt.test.TestDetektion
import dev.detekt.test.utils.StringPrintStream
import dev.detekt.test.utils.resourceAsPath
import dev.detekt.tooling.api.spec.ReportsSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

private const val CONSOLE_OUTPUT = "console report output"

class OutputFacadeSpec {
    @Test
    fun `writes output and console reports`(@TempDir tempDir: Path) {
        val result = runFacade(tempDir, OutputFacade.ReportPaths.Hidden)

        assertThat(result.output)
            .contains(CONSOLE_OUTPUT)
            .doesNotContain(result.reportPathMessages)
        assertThat(result.reportFiles).allSatisfy { assertThat(it.path).isNotEmptyFile() }
    }

    @Test
    fun `shows output report paths when requested`(@TempDir tempDir: Path) {
        val result = runFacade(tempDir, OutputFacade.ReportPaths.Show)

        assertThat(result.output).contains(result.reportPathMessages)
    }

    @Test
    fun `does not write null output and console reports`(@TempDir tempDir: Path) {
        val printStream = StringPrintStream()
        val outputPath = tempDir.resolve("detekt.null")
        val reportFile = ReportFile(NullOutputReport().id, outputPath)

        createProcessingSettings(
            reportPaths = listOf(reportFile),
            outputChannel = printStream
        ) {
            logging { debug = false }
        }.use { OutputFacade(it).run(TestDetektion()) }

        assertThat(outputPath).doesNotExist()
        assertThat(printStream.toString()).isEmpty()
    }

    @Test
    fun `rejects reports that use the same path`(@TempDir tempDir: Path) {
        val outputPath = tempDir.resolve("detekt.html")
        val reportFiles = listOf(
            ReportFile(HtmlOutputReport().id, outputPath),
            ReportFile(CheckstyleOutputReport().id, outputPath)
        )

        assertThatCode {
            createProcessingSettings(reportPaths = reportFiles).use { OutputFacade(it) }
        }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("The path $outputPath is defined in multiple reports: [html, checkstyle]")
    }

    @Test
    fun `does not write console reports when disabled`() {
        val printStream = StringPrintStream()
        val spec = createNullLoggingSpec {
            config {
                configPaths = listOf(resourceAsPath("/reporting/disabled-reports.yml"))
            }
            logging { outputChannel = printStream }
        }

        spec.withSettings { OutputFacade(this).run(createResult()) }

        assertThat(printStream.toString()).isEmpty()
    }

    private fun runFacade(tempDir: Path, reportPaths: OutputFacade.ReportPaths): FacadeRun {
        val reportFiles = listOf(
            ReportFile(CheckstyleOutputReport().id, tempDir.resolve("detekt.xml")),
            ReportFile(HtmlOutputReport().id, tempDir.resolve("detekt.html")),
            ReportFile(MarkdownOutputReport().id, tempDir.resolve("detekt.md")),
            ReportFile(SarifOutputReport().id, tempDir.resolve("detekt.sarif")),
        )
        val outputChannel = StringPrintStream()

        createProcessingSettings(
            reportPaths = reportFiles,
            outputChannel = outputChannel
        ) {
            logging { debug = false }
        }.use { OutputFacade(it).run(createResult(), reportPaths) }

        return FacadeRun(outputChannel.toString(), reportFiles)
    }

    private fun createResult(): Detektion =
        TestDetektion(
            createIssue(
                createRuleInstance(ruleSetId = "Key"),
                createIssueEntity(createIssueLocation("TestFile.kt"))
            ),
            notifications = listOf(Notification(CONSOLE_OUTPUT, Level.Error))
        )

    private data class FacadeRun(val output: String, val reportFiles: List<ReportFile>) {
        val reportPathMessages: List<String> = reportFiles.map(ReportFile::generatedMessage)
    }

    private data class ReportFile(override val type: String, override val path: Path) : ReportsSpec.Report {
        val generatedMessage: String = "Successfully generated $type at ${path.toUri()}"
    }
}

class NullOutputReport : OutputReport {
    override val id: String = "null-output"

    override fun render(detektion: Detektion): String? = null
}

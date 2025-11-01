package dev.detekt.core.reporting

import dev.detekt.api.testfixtures.TestDetektion
import dev.detekt.api.testfixtures.createIssue
import dev.detekt.api.testfixtures.createIssueEntity
import dev.detekt.api.testfixtures.createIssueLocation
import dev.detekt.api.testfixtures.createRuleInstance
import dev.detekt.core.createNullLoggingSpec
import dev.detekt.core.tooling.withSettings
import dev.detekt.report.html.HtmlOutputReport
import dev.detekt.report.markdown.MarkdownOutputReport
import dev.detekt.report.sarif.SarifOutputReport
import dev.detekt.report.xml.CheckstyleOutputReport
import dev.detekt.test.utils.StringPrintStream
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.nio.file.Path

class OutputFacadeSpec {

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `Running the output facade with multiple reports`(showReports: Boolean, @TempDir tempDir: Path) {
        val printStream = StringPrintStream()
        val defaultResult = TestDetektion(
            createIssue(
                createRuleInstance(ruleSetId = "Key"),
                createIssueEntity(createIssueLocation("TestFile.kt"))
            ),
        )
        val htmlOutputPath = tempDir.resolve("detekt.html")
        val xmlOutputPath = tempDir.resolve("detekt.xml")
        val markdownOutputPath = tempDir.resolve("detekt.md")
        val sarifOutputPath = tempDir.resolve("detekt.sarif")

        val spec = createNullLoggingSpec {
            reports {
                report { "html" to htmlOutputPath }
                report { "checkstyle" to xmlOutputPath }
                report { "markdown" to markdownOutputPath }
                report { "sarif" to sarifOutputPath }
            }
            logging {
                outputChannel = printStream
            }
        }

        spec.withSettings { OutputFacade(this, showReports).run(defaultResult) }

        val expected = listOf(
            "Successfully generated ${CheckstyleOutputReport().id} at ${xmlOutputPath.toUri()}",
            "Successfully generated ${HtmlOutputReport().id} at ${htmlOutputPath.toUri()}",
            "Successfully generated ${MarkdownOutputReport().id} at ${markdownOutputPath.toUri()}",
            "Successfully generated ${SarifOutputReport().id} at ${sarifOutputPath.toUri()}",
        )

        if (showReports) {
            assertThat(printStream.toString()).contains(expected)
        } else {
            assertThat(printStream.toString()).doesNotContain(expected)
        }
        assertThat(xmlOutputPath).isNotEmptyFile()
        assertThat(htmlOutputPath).isNotEmptyFile()
        assertThat(markdownOutputPath).isNotEmptyFile()
    }

    @Test
    fun `two reports can't have the same path`(@TempDir tempDir: Path) {
        val printStream = StringPrintStream()
        val defaultResult = TestDetektion(
            createIssue(
                createRuleInstance(ruleSetId = "Key"),
                createIssueEntity(createIssueLocation("TestFile.kt"))
            ),
        )
        val htmlOutputPath = tempDir.resolve("detekt.html")
        val markdownOutputPath = tempDir.resolve("detekt.md")

        val spec = createNullLoggingSpec {
            reports {
                report { "html" to htmlOutputPath }
                report { "checkstyle" to htmlOutputPath }
                report { "markdown" to markdownOutputPath }
            }
            logging {
                outputChannel = printStream
            }
        }

        assertThatCode {
            spec.withSettings { OutputFacade(this, true).run(defaultResult) }
        }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("The path $htmlOutputPath is defined in multiple reports: [html, checkstyle]")
    }

    @Test
    fun `three reports can't have the same path`(@TempDir tempDir: Path) {
        val printStream = StringPrintStream()
        val defaultResult = TestDetektion(
            createIssue(
                createRuleInstance(ruleSetId = "Key"),
                createIssueEntity(createIssueLocation("TestFile.kt"))
            ),
        )
        val htmlOutputPath = tempDir.resolve("detekt.html")
        val sarifOutputPath = tempDir.resolve("detekt.sarif")

        val spec = createNullLoggingSpec {
            reports {
                report { "html" to htmlOutputPath }
                report { "checkstyle" to htmlOutputPath }
                report { "markdown" to htmlOutputPath }
                report { "sarif" to sarifOutputPath }
            }
            logging {
                outputChannel = printStream
            }
        }

        assertThatCode {
            spec.withSettings { OutputFacade(this, true).run(defaultResult) }
        }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("The path $htmlOutputPath is defined in multiple reports: [html, checkstyle, markdown]")
    }
}

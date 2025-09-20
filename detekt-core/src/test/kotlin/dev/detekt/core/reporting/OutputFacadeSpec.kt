package dev.detekt.core.reporting

import dev.detekt.api.testfixtures.TestDetektion
import dev.detekt.api.testfixtures.createIssue
import dev.detekt.api.testfixtures.createIssueEntity
import dev.detekt.api.testfixtures.createIssueLocation
import dev.detekt.api.testfixtures.createRuleInstance
import dev.detekt.core.createNullLoggingSpec
import dev.detekt.core.tooling.withSettings
import dev.detekt.report.html.HtmlOutputReport
import dev.detekt.report.md.MdOutputReport
import dev.detekt.report.sarif.SarifOutputReport
import dev.detekt.report.xml.CheckstyleOutputReport
import dev.detekt.test.utils.StringPrintStream
import dev.detekt.test.utils.createTempFileForTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test

class OutputFacadeSpec {

    @Test
    fun `Running the output facade with multiple reports`() {
        val printStream = StringPrintStream()
        val defaultResult = TestDetektion(
            createIssue(
                createRuleInstance(ruleSetId = "Key"),
                createIssueEntity(createIssueLocation("TestFile.kt"))
            ),
        )
        val htmlOutputPath = createTempFileForTest("detekt", ".html")
        val xmlOutputPath = createTempFileForTest("detekt", ".xml")
        val mdOutputPath = createTempFileForTest("detekt", ".md")
        val sarifOutputPath = createTempFileForTest("detekt", ".sarif")

        val spec = createNullLoggingSpec {
            reports {
                report { "html" to htmlOutputPath }
                report { "checkstyle" to xmlOutputPath }
                report { "md" to mdOutputPath }
                report { "sarif" to sarifOutputPath }
            }
            logging {
                outputChannel = printStream
            }
        }

        spec.withSettings { OutputFacade(this).run(defaultResult) }

        assertThat(printStream.toString()).contains(
            "Successfully generated ${CheckstyleOutputReport().id} at ${xmlOutputPath.toUri()}",
            "Successfully generated ${HtmlOutputReport().id} at ${htmlOutputPath.toUri()}",
            "Successfully generated ${MdOutputReport().id} at ${mdOutputPath.toUri()}",
            "Successfully generated ${SarifOutputReport().id} at ${sarifOutputPath.toUri()}",
        )
        assertThat(xmlOutputPath).isNotEmptyFile()
        assertThat(htmlOutputPath).isNotEmptyFile()
        assertThat(mdOutputPath).isNotEmptyFile()
    }

    @Test
    fun `two reports can't have the same path`() {
        val printStream = StringPrintStream()
        val defaultResult = TestDetektion(
            createIssue(
                createRuleInstance(ruleSetId = "Key"),
                createIssueEntity(createIssueLocation("TestFile.kt"))
            ),
        )
        val htmlOutputPath = createTempFileForTest("detekt", ".html")
        val mdOutputPath = createTempFileForTest("detekt", ".md")

        val spec = createNullLoggingSpec {
            reports {
                report { "html" to htmlOutputPath }
                report { "checkstyle" to htmlOutputPath }
                report { "md" to mdOutputPath }
            }
            logging {
                outputChannel = printStream
            }
        }

        assertThatCode {
            spec.withSettings { OutputFacade(this).run(defaultResult) }
        }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("The path $htmlOutputPath is defined in multiple reports: [html, checkstyle]")
    }

    @Test
    fun `three reports can't have the same path`() {
        val printStream = StringPrintStream()
        val defaultResult = TestDetektion(
            createIssue(
                createRuleInstance(ruleSetId = "Key"),
                createIssueEntity(createIssueLocation("TestFile.kt"))
            ),
        )
        val htmlOutputPath = createTempFileForTest("detekt", ".html")
        val sarifOutputPath = createTempFileForTest("detekt", ".sarif")

        val spec = createNullLoggingSpec {
            reports {
                report { "html" to htmlOutputPath }
                report { "checkstyle" to htmlOutputPath }
                report { "md" to htmlOutputPath }
                report { "sarif" to sarifOutputPath }
            }
            logging {
                outputChannel = printStream
            }
        }

        assertThatCode {
            spec.withSettings { OutputFacade(this).run(defaultResult) }
        }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("The path $htmlOutputPath is defined in multiple reports: [html, checkstyle, md]")
    }
}

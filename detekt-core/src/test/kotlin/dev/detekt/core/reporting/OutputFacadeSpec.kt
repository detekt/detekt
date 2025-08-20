package dev.detekt.core.reporting

import dev.detekt.api.testfixtures.createIssue
import dev.detekt.api.testfixtures.createIssueEntity
import dev.detekt.api.testfixtures.createIssueLocation
import dev.detekt.api.testfixtures.createRuleInstance
import dev.detekt.core.DetektResult
import dev.detekt.core.createNullLoggingSpec
import dev.detekt.core.tooling.withSettings
import dev.detekt.report.html.HtmlOutputReport
import dev.detekt.report.md.MdOutputReport
import dev.detekt.report.xml.XmlOutputReport
import dev.detekt.test.utils.StringPrintStream
import dev.detekt.test.utils.createTempFileForTest
import dev.detekt.test.utils.resourceAsPath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Path

class OutputFacadeSpec {

    @Test
    fun `Running the output facade with multiple reports`() {
        val printStream = StringPrintStream()
        val inputPath: Path = resourceAsPath("/cases")
        val defaultResult = DetektResult(
            issues = listOf(
                createIssue(
                    createRuleInstance(ruleSetId = "Key"),
                    createIssueEntity(location = createIssueLocation("TestFile.kt"))
                )
            ),
            rules = emptyList(),
        )
        val htmlOutputPath = createTempFileForTest("detekt", ".html")
        val xmlOutputPath = createTempFileForTest("detekt", ".xml")
        val mdOutputPath = createTempFileForTest("detekt", ".md")

        val spec = createNullLoggingSpec {
            project {
                inputPaths = listOf(inputPath)
            }
            reports {
                report { "html" to htmlOutputPath }
                report { "xml" to xmlOutputPath }
                report { "md" to mdOutputPath }
            }
            logging {
                outputChannel = printStream
            }
        }

        spec.withSettings { OutputFacade(this).run(defaultResult) }

        assertThat(printStream.toString()).contains(
            "Successfully generated ${XmlOutputReport().id} at ${xmlOutputPath.toUri()}",
            "Successfully generated ${HtmlOutputReport().id} at ${htmlOutputPath.toUri()}",
            "Successfully generated ${MdOutputReport().id} at ${mdOutputPath.toUri()}"
        )
    }
}

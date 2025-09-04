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
import dev.detekt.report.xml.XmlOutputReport
import dev.detekt.test.utils.StringPrintStream
import dev.detekt.test.utils.createTempFileForTest
import org.assertj.core.api.Assertions.assertThat
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

        val spec = createNullLoggingSpec {
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
        assertThat(xmlOutputPath).isNotEmptyFile()
        assertThat(htmlOutputPath).isNotEmptyFile()
        assertThat(mdOutputPath).isNotEmptyFile()
    }
}

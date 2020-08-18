package io.gitlab.arturbosch.detekt.core.reporting

import io.github.detekt.report.html.HtmlOutputReport
import io.github.detekt.report.txt.TxtOutputReport
import io.github.detekt.report.xml.XmlOutputReport
import io.github.detekt.test.utils.StringPrintStream
import io.github.detekt.test.utils.createTempFileForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.core.DetektResult
import io.gitlab.arturbosch.detekt.core.createNullLoggingSpec
import io.gitlab.arturbosch.detekt.core.tooling.withSettings
import io.gitlab.arturbosch.detekt.test.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import java.nio.file.Path

internal class OutputFacadeSpec : Spek({

    test("Running the output facade with multiple reports") {
        val printStream = StringPrintStream()
        val inputPath: Path = resourceAsPath("/cases")
        val defaultResult = DetektResult(mapOf(Pair("Key", listOf(createFinding()))))
        val plainOutputPath = createTempFileForTest("detekt", ".txt")
        val htmlOutputPath = createTempFileForTest("detekt", ".html")
        val xmlOutputPath = createTempFileForTest("detekt", ".xml")

        val spec = createNullLoggingSpec {
            project {
                inputPaths = listOf(inputPath)
            }
            reports {
                report { "html" to htmlOutputPath }
                report { "txt" to plainOutputPath }
                report { "xml" to xmlOutputPath }
            }
            logging {
                outputChannel = printStream
            }
        }

        spec.withSettings { OutputFacade(this).run(defaultResult) }

        assertThat(printStream.toString()).contains(
            "Successfully generated ${TxtOutputReport().name} at $plainOutputPath",
            "Successfully generated ${XmlOutputReport().name} at $xmlOutputPath",
            "Successfully generated ${HtmlOutputReport().name} at $htmlOutputPath"
        )
    }
})

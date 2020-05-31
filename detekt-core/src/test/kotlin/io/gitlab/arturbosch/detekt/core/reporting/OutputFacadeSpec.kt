package io.gitlab.arturbosch.detekt.core.reporting

import io.github.detekt.report.html.HtmlOutputReport
import io.github.detekt.report.txt.TxtOutputReport
import io.github.detekt.report.xml.XmlOutputReport
import io.github.detekt.test.utils.StringPrintStream
import io.github.detekt.test.utils.createTempFileForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.core.DetektResult
import io.gitlab.arturbosch.detekt.test.createFinding
import io.gitlab.arturbosch.detekt.test.createProcessingSettings
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.utils.closeQuietly
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Path

internal class OutputFacadeSpec : Spek({

    describe("Running the output facade with multiple reports") {

        val printStream = StringPrintStream()
        val inputPath: Path = resourceAsPath("/cases")
        lateinit var plainOutputPath: Path
        lateinit var htmlOutputPath: Path
        lateinit var xmlOutputPath: Path

        val defaultDetektion = DetektResult(mapOf(Pair("Key", listOf(createFinding()))))
        val defaultSettings = createProcessingSettings(inputPath, outPrinter = printStream)

        lateinit var reportPaths: List<ReportPath>

        beforeEachTest {
            plainOutputPath = createTempFileForTest("detekt", ".txt")
            htmlOutputPath = createTempFileForTest("detekt", ".html")
            xmlOutputPath = createTempFileForTest("detekt", ".xml")
            reportPaths = listOf(
                "xml:$xmlOutputPath",
                "txt:$plainOutputPath",
                "html:$htmlOutputPath"
            ).map { ReportPath.from(it) }
        }

        afterGroup {
            closeQuietly(defaultSettings)
        }

        it("creates all output files") {
            val subject = OutputFacade(reportPaths, defaultDetektion, defaultSettings)

            subject.run()

            assertThat(printStream.toString()).contains(
                "Successfully generated ${TxtOutputReport().name} at $plainOutputPath$LN",
                "Successfully generated ${XmlOutputReport().name} at $xmlOutputPath$LN",
                "Successfully generated ${HtmlOutputReport().name} at $htmlOutputPath$LN")
        }
    }
})

private val LN = System.lineSeparator()

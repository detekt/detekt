package io.gitlab.arturbosch.detekt.core.reporting

import io.github.detekt.report.html.HtmlOutputReport
import io.github.detekt.report.txt.TxtOutputReport
import io.github.detekt.report.xml.XmlOutputReport
import io.github.detekt.test.utils.StringPrintStream
import io.github.detekt.test.utils.createTempFileForTest
import io.github.detekt.test.utils.resourceAsPath
import io.github.detekt.tooling.dsl.ReportsSpecBuilder
import io.gitlab.arturbosch.detekt.core.DetektResult
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.createProcessingSettings
import io.gitlab.arturbosch.detekt.test.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.utils.closeQuietly
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Path

internal class OutputFacadeSpec : Spek({

    describe("Running the output facade with multiple reports") {

        val printStream = StringPrintStream()
        val inputPath: Path = resourceAsPath("/cases")
        val defaultDetektion = DetektResult(mapOf(Pair("Key", listOf(createFinding()))))

        lateinit var defaultSettings: ProcessingSettings
        lateinit var plainOutputPath: Path
        lateinit var htmlOutputPath: Path
        lateinit var xmlOutputPath: Path

        beforeEachTest {
            plainOutputPath = createTempFileForTest("detekt", ".txt")
            htmlOutputPath = createTempFileForTest("detekt", ".html")
            xmlOutputPath = createTempFileForTest("detekt", ".xml")
            val reportsSpec = ReportsSpecBuilder().apply {
                report { "html" to htmlOutputPath }
                report { "txt" to plainOutputPath }
                report { "xml" to xmlOutputPath }
            }.build()
            defaultSettings = createProcessingSettings(
                inputPath,
                outPrinter = printStream,
                reportPaths = reportsSpec.reports
            )
        }

        afterGroup {
            closeQuietly(defaultSettings)
        }

        it("creates all output files") {
            val subject = OutputFacade(defaultSettings)

            subject.run(defaultDetektion)

            assertThat(printStream.toString()).contains(
                "Successfully generated ${TxtOutputReport().name} at $plainOutputPath$LN",
                "Successfully generated ${XmlOutputReport().name} at $xmlOutputPath$LN",
                "Successfully generated ${HtmlOutputReport().name} at $htmlOutputPath$LN")
        }
    }
})

private val LN = System.lineSeparator()

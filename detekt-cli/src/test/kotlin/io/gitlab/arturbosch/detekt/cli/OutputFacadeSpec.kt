package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.cli.out.HtmlOutputReport
import io.gitlab.arturbosch.detekt.cli.out.TxtOutputReport
import io.gitlab.arturbosch.detekt.cli.out.XmlOutputReport
import io.gitlab.arturbosch.detekt.core.DetektResult
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.nio.file.Path
import java.nio.file.Paths

internal class OutputFacadeSpec : Spek({

    val outputStream = ByteArrayOutputStream()
    val inputPath: Path = Paths.get(resource("/cases"))
    val plainOutputPath = File.createTempFile("detekt", ".txt")
    val htmlOutputPath = File.createTempFile("detekt", ".html")
    val xmlOutputPath = File.createTempFile("detekt", ".xml")

    val defaultDetektion = DetektResult(mapOf(Pair("Key", listOf(createFinding()))))
    val defaultSettings = ProcessingSettings(inputPath, outPrinter = PrintStream(outputStream))

    describe("Running the output facade") {

        describe("with multiple reports") {
            val cliArgs = CliArgs.parse(
                arrayOf(
                    "--input", inputPath.toString(),
                    "--report", "xml:$xmlOutputPath",
                    "--report", "txt:$plainOutputPath",
                    "--report", "html:$htmlOutputPath"
                )
            )

            it("creates all output files") {
                val subject = OutputFacade(cliArgs, defaultDetektion, defaultSettings)

                subject.run()

                outputStream.assertThatItPrintsReportPath(TxtOutputReport().name)
                outputStream.assertThatItPrintsReportPath(XmlOutputReport().name)
                outputStream.assertThatItPrintsReportPath(HtmlOutputReport().name)
            }
        }
    }
})

private fun ByteArrayOutputStream.assertThatItPrintsReportPath(reportName: String) {
    val outputString = toString(Charsets.UTF_8.name())
    assertThat(outputString).contains("Successfully generated $reportName at ")
}

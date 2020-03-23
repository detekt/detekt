package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.cli.out.HtmlOutputReport
import io.gitlab.arturbosch.detekt.cli.out.TxtOutputReport
import io.gitlab.arturbosch.detekt.cli.out.XmlOutputReport
import io.gitlab.arturbosch.detekt.core.DetektResult
import io.gitlab.arturbosch.detekt.test.StringPrintStream
import io.gitlab.arturbosch.detekt.test.createProcessingSettings
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.utils.closeQuietly
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

internal class OutputFacadeSpec : Spek({

    val printStream = StringPrintStream()
    val inputPath: Path = Paths.get(resource("/cases"))
    val plainOutputPath = File.createTempFile("detekt", ".txt")
    val htmlOutputPath = File.createTempFile("detekt", ".html")
    val xmlOutputPath = File.createTempFile("detekt", ".xml")

    val defaultDetektion = DetektResult(mapOf(Pair("Key", listOf(createFinding()))))
    val defaultSettings = createProcessingSettings(inputPath, outPrinter = printStream)

    afterGroup { closeQuietly(defaultSettings) }

    describe("Running the output facade") {

        describe("with multiple reports") {
            val cliArgs = createCliArgs(
                "--input", inputPath.toString(),
                "--report", "xml:$xmlOutputPath",
                "--report", "txt:$plainOutputPath",
                "--report", "html:$htmlOutputPath"
            )

            it("creates all output files") {
                val subject = OutputFacade(cliArgs, defaultDetektion, defaultSettings)

                subject.run()

                assertThat(printStream.toString()).contains(
                    "Successfully generated ${TxtOutputReport().name} at $plainOutputPath$LN",
                    "Successfully generated ${XmlOutputReport().name} at $xmlOutputPath$LN",
                    "Successfully generated ${HtmlOutputReport().name} at $htmlOutputPath$LN")
            }
        }
    }
})

private val LN = System.lineSeparator()

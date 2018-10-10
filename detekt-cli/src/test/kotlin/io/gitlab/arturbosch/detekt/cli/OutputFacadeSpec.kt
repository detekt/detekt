package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.cli.out.HtmlOutputReport
import io.gitlab.arturbosch.detekt.cli.out.TxtOutputReport
import io.gitlab.arturbosch.detekt.cli.out.XmlOutputReport
import io.gitlab.arturbosch.detekt.core.DetektResult
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.subject.SubjectSpek
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Sebastiano Poggi
 */
internal class OutputFacadeSpec : SubjectSpek<OutputFacade>({

	val outputStream = ByteArrayOutputStream()
	val inputPath: Path = Paths.get(resource("/cases"))
	val plainOutputPath = File.createTempFile("detekt", ".txt")
	val htmlOutputPath = File.createTempFile("detekt", ".html")
	val xmlOutputPath = File.createTempFile("detekt", ".xml")

	subject {
		val args = arrayOf(
				"--input", inputPath.toString(),
				"--report", "xml:$xmlOutputPath",
				"--report", "txt:$plainOutputPath",
				"--report", "html:$htmlOutputPath"
		)

		val (cliArgs, _) = parseArguments<CliArgs>(args)
		val settings = ProcessingSettings(inputPath, outPrinter = PrintStream(outputStream))
		val detektion = DetektResult(mapOf(Pair("Key", listOf(createFinding()))))
		OutputFacade(cliArgs, detektion, settings)
	}

	describe("prints the reports paths when writing them") {
		subject.run()

		outputStream.assertThatItPrintsReportPath(TxtOutputReport().name)
		outputStream.assertThatItPrintsReportPath(XmlOutputReport().name)
		outputStream.assertThatItPrintsReportPath(HtmlOutputReport().name)
	}
})

fun ByteArrayOutputStream.assertThatItPrintsReportPath(reportName: String) {
	val outputString = toString(Charsets.UTF_8.name())
	assertThat(outputString.contains("Successfully generated $reportName at ")).isTrue()
}

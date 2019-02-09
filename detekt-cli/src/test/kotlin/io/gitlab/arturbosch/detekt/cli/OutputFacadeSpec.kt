package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.cli.console.BuildFailure
import io.gitlab.arturbosch.detekt.cli.out.HtmlOutputReport
import io.gitlab.arturbosch.detekt.cli.out.TxtOutputReport
import io.gitlab.arturbosch.detekt.cli.out.XmlOutputReport
import io.gitlab.arturbosch.detekt.core.DetektResult
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.test.resource
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.nio.file.Path
import java.nio.file.Paths
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Sebastiano Poggi
 */
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
            val cliArgs = listOf(
                    "--input", inputPath.toString(),
                    "--report", "xml:$xmlOutputPath",
                    "--report", "txt:$plainOutputPath",
                    "--report", "html:$htmlOutputPath"
            ).toCliArgs()

            it("creates all output files") {
                val subject = OutputFacade(cliArgs, defaultDetektion, defaultSettings)

                subject.run()

                outputStream.assertThatItPrintsReportPath(TxtOutputReport().name)
                outputStream.assertThatItPrintsReportPath(XmlOutputReport().name)
                outputStream.assertThatItPrintsReportPath(HtmlOutputReport().name)
            }
        }

        describe("with more findings than issues allowed") {
            it("reports build failure for default task") {
                val cliArgs = listOf("--input", inputPath.toString()).toCliArgs()
                val subject = OutputFacade(cliArgs, defaultDetektion, ProcessingSettings(inputPath, config = ZeroMaxIssuesConfig))

                assertThatExceptionOfType(BuildFailure::class.java).isThrownBy { subject.run() }
            }
            it("does not throw an exception for createBaseline task") {
                val cliArgs = listOf("--create-baseline", "--input", inputPath.toString()).toCliArgs()
                val subject = OutputFacade(cliArgs, defaultDetektion, ProcessingSettings(inputPath, config = ZeroMaxIssuesConfig))

                assertThatCode { subject.run() }.doesNotThrowAnyException()
            }
        }
    }
})

private fun List<String>.toCliArgs(): CliArgs {
    val (cliArgs, _) = parseArguments<CliArgs>(this.toTypedArray())
    return cliArgs
}

private object ZeroMaxIssuesConfig : Config {
    override fun <T : Any> valueOrNull(key: String): T? = Config.empty.valueOrNull(key)
    override fun subConfig(key: String) = this
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> valueOrDefault(key: String, default: T): T = when (key) {
        "maxIssues" -> 0 as T
        else -> Config.empty.valueOrDefault(key, default)
    }
}

private fun ByteArrayOutputStream.assertThatItPrintsReportPath(reportName: String) {
    val outputString = toString(Charsets.UTF_8.name())
    assertThat(outputString.contains("Successfully generated $reportName at ")).isTrue()
}

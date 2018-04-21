package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.cli.baseline.BaselineFacade
import io.gitlab.arturbosch.detekt.core.ProcessingSettings

/**
 * @author Artur Bosch
 */
class OutputFacade(private val arguments: CliArgs,
				   private val detektion: Detektion,
				   private val settings: ProcessingSettings) {

	private val printStream = System.out
	private val config = settings.config
	private val baselineFacade = arguments.baseline?.let { BaselineFacade(it) }
	private val createBaseline = arguments.createBaseline
	private val fileName = arguments.outputName

	fun run() {
		if (createBaseline) {
			val smells = detektion.findings.flatMap { it.value }
			baselineFacade?.create(smells)
		}

		val result = if (baselineFacade != null) {
			FilteredDetectionResult(detektion, baselineFacade)
		} else detektion

		val reports = ReportLocator(settings).load()
		reports.sortedBy { it.priority }.asReversed().forEach { report ->
			report.init(config)
			when (report) {
				is ConsoleReport -> handleConsoleReport(report, result)
				is OutputReport -> handleOutputReport(report, result)
			}
		}
	}

	private fun handleOutputReport(report: OutputReport, result: Detektion) {
		arguments.output?.let {
			fileName?.let { report.fileName = it }
			val filePath = it.resolve("${report.fileName}.${report.ending}")
			report.write(filePath, result)
			printStream.println("Successfully generated ${report.id} at $filePath")
		}
	}

	private fun handleConsoleReport(report: ConsoleReport, result: Detektion) {
		report.print(printStream, result)
	}
}


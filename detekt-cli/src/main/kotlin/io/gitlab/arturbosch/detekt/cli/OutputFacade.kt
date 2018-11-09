package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.cli.baseline.BaselineFacade
import io.gitlab.arturbosch.detekt.cli.console.BuildFailureReport
import io.gitlab.arturbosch.detekt.core.ProcessingSettings

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
class OutputFacade(arguments: CliArgs,
				   private val detektion: Detektion,
				   private val settings: ProcessingSettings) {

	private val printStream = settings.outPrinter
	private val config = settings.config
	private val baselineFacade = arguments.baseline?.let { BaselineFacade(it, arguments.sourceSetId) }
	private val createBaseline = arguments.createBaseline
	private val reportPaths = arguments.reportPaths.toHashMap({ it.kind }, { it.path })

	fun run() {
		if (createBaseline) {
			val smells = detektion.findings.flatMap { it.value }
			baselineFacade?.create(smells)
		}

		val result = if (baselineFacade != null) {
			FilteredDetectionResult(detektion, baselineFacade)
		} else detektion

		val reports = ReportLocator(settings)
				.load()
				.filterNot { createBaseline && it is BuildFailureReport }
				.sortedBy { it.priority }
				.asReversed()

		reports.forEach { report ->
			report.init(config)
			when (report) {
				is ConsoleReport -> handleConsoleReport(report, result)
				is OutputReport -> handleOutputReport(report, result)
			}
		}
	}

	private fun handleOutputReport(report: OutputReport, result: Detektion) {
		val filePath = reportPaths[report.id]
		if (filePath != null) {
			report.write(filePath, result)
			printStream.println("Successfully generated ${report.name} at $filePath")
		}
	}

	private fun handleConsoleReport(report: ConsoleReport, result: Detektion) {
		report.print(printStream, result)
	}
}

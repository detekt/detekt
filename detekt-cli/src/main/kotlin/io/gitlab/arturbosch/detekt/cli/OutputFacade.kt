package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.cli.baseline.BaselineFacade
import io.gitlab.arturbosch.detekt.cli.out.HtmlOutputReport
import io.gitlab.arturbosch.detekt.cli.out.XmlOutputReport
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import java.io.File

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
class OutputFacade(private val arguments: Args,
				   private val detektion: Detektion,
				   private val settings: ProcessingSettings) {

	private val printStream = System.out
	private val config = settings.config
	private val baselineFacade = arguments.baseline?.let { BaselineFacade(it) }
	private val createBaseline = arguments.createBaseline

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
			}
		}

		arguments.htmlReport?.let {
			HtmlOutputReport().write(File(it).toPath(), result)
		}

		arguments.xmlReport?.let {
			XmlOutputReport().write(File(it).toPath(), result)
		}

		arguments.plainReport?.let {
			XmlOutputReport().write(File(it).toPath(), result)
		}
	}

	private fun handleConsoleReport(report: ConsoleReport, result: Detektion) {
		report.print(printStream, result)
	}
}


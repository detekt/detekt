package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.cli.baseline.BaselineFacade
import io.gitlab.arturbosch.detekt.core.ProcessingSettings

/**
 * @author Artur Bosch
 */
class OutputFacade(private val main: Main,
				   private val detektion: Detektion,
				   private val settings: ProcessingSettings) {

	private val config = settings.config
	private val baselineFacade = main.baseline?.let { BaselineFacade(it) }
	private val createBaseline = main.createBaseline

	fun run() {
		val reports = ReportLocator(settings).load()
		reports.sortedBy { it.priority }.asReversed().forEach { report ->
			report.init(config)
			when (report) {
				is ConsoleReport -> report.print(System.out, detektion)
				is OutputReport -> main.output?.apply { report.write(this, detektion) }
			}
		}

		val smells = detektion.findings.flatMap { it.value }
		if (createBaseline) baselineFacade?.create(smells)
	}
}

package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.cli.baseline.BaselineFacade
import io.gitlab.arturbosch.detekt.core.ProcessingSettings

/**
 * @author Artur Bosch
 */
class OutputFacade(private val arguments: Args,
				   private val detektion: Detektion,
				   private val settings: ProcessingSettings) {

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
				is ConsoleReport -> report.print(System.out, result)
				is OutputReport -> arguments.output?.apply { report.write(this, result) }
			}
		}
	}
}


package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.ProcessingSettings

interface Executable {
	fun execute()
}

/**
 * @author Artur Bosch
 */
class Runner(private val main: Main) : Executable {

	override fun execute() {
		val (settings, config) = createSettingsAndConfig()

		val start = System.currentTimeMillis()
		val detektion = DetektFacade.instance(settings).run()
		val end = System.currentTimeMillis() - start

		val reports = ReportLocator(settings).load()
		reports.sortedBy { it.priority }.asReversed().forEach { report ->
			report.init(config)
			when (report) {
				is ConsoleReport -> report.print(System.out, detektion)
				is OutputReport -> main.output?.apply { report.write(this, detektion) }
			}
		}

		println("\ndetekt run within $end ms")
	}

	private fun createSettingsAndConfig(): Pair<ProcessingSettings, Config> {
		with(main) {
			val pathFilters = createPathFilters()
			val rules = createRulePaths()
			val config = loadConfiguration()
			return ProcessingSettings(inputPath, config, pathFilters, parallel,
					disableDefaultRuleSets, rules) to config
		}
	}
}

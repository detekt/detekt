package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.OutputFormat
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import java.util.ServiceLoader

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

		val reports = ReportLocator(settings).load()
		for (report in reports) {
			report.init(config)
			when (report) {
				is ConsoleReport -> report.print(System.out, detektion)
				is OutputFormat -> main.output?.apply { report.write(this, detektion) }
			}
		}
		val end = System.currentTimeMillis() - start

		println("\ndetekt run within $end ms")
	}

	private fun createSettingsAndConfig(): Pair<ProcessingSettings, Config> {
		with(main) {
			val pathFilters = createPathFilters()
			val rules = createRulePaths()
			val config = loadConfiguration()
			val changeListeners = createProcessors()
			return ProcessingSettings(inputPath, config, pathFilters, parallel,
					disableDefaultRuleSets, rules, changeListeners) to config
		}
	}

	private fun createProcessors() = ServiceLoader.load(FileProcessListener::class.java).toList()

}

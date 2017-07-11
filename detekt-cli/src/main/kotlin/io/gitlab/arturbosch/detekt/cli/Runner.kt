package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.ProjectComplexityProcessor
import io.gitlab.arturbosch.detekt.core.ProjectLLOCProcessor

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
		val facade = OutputFacade(main, config, detektion)

		facade.run {
			consoleFacade()
			reportFacade()
		}

		val end = System.currentTimeMillis() - start

		println("\ndetekt run within $end ms")
		facade.buildErrorCheck()
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

	private fun createProcessors() = listOf(ProjectLLOCProcessor(), ProjectComplexityProcessor(), DetektProgressListener())

}

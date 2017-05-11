package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.ProjectComplexityProcessor
import io.gitlab.arturbosch.detekt.core.ProjectLLOCProcessor

/**
 * @author Artur Bosch
 */
class Runner(private val main: Main) {

	fun execute() {
		val (settings, config) = createSettingsAndConfig()

		val start = System.currentTimeMillis()
		val detektion = DetektFacade.instance(settings).run()
		Output(detektion, main).report()
		val end = System.currentTimeMillis() - start

		println("\ndetekt run within $end ms")
		SmellThreshold(config, main).check(detektion)
	}

	private fun createSettingsAndConfig(): Pair<ProcessingSettings, Config> {
		with(main) {
			val pathFilters = createPathFilters()
			val rules = createRulePaths()
			val config = loadConfiguration()
			val changeListeners = createProcessors()
			return ProcessingSettings(project, config, pathFilters, parallel,
					disableDefaultRuleSets, rules, changeListeners) to config
		}
	}

	private fun createProcessors() = listOf(ProjectLLOCProcessor(), ProjectComplexityProcessor(), DetektProgressListener())

}

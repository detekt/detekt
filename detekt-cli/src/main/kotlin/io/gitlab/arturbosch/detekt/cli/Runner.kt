package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.ProcessingSettings

interface Executable {
	fun execute()
}

/**
 * @author Artur Bosch
 */
class Runner(private val arguments: Args) : Executable {

	override fun execute() {
		val settings = createSettingsAndConfig()

		val start = System.currentTimeMillis()
		val detektion = DetektFacade.instance(settings).run()
		val end = System.currentTimeMillis() - start

		OutputFacade(arguments, detektion, settings).run()
		println("\ndetekt run within $end ms")
	}

	private fun createSettingsAndConfig(): ProcessingSettings {
		with(arguments) {
			val pathFilters = createPathFilters()
			val rules = createRulePaths()
			val config = loadConfiguration()
			return ProcessingSettings(inputPath, config, pathFilters, parallel, disableDefaultRuleSets, rules)
		}
	}
}

package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import kotlin.system.measureTimeMillis

interface Executable {
	fun execute()
}

/**
 * @author Artur Bosch
 */
class Runner(private val arguments: Args) : Executable {

	override fun execute() {
		val settings = createSettings()

		val time = measureTimeMillis {
			val detektion = DetektFacade.instance(settings).run()
			OutputFacade(arguments, detektion, settings).run()
		}

		println("\ndetekt finished in $time ms.")
	}

	private fun createSettings(): ProcessingSettings {
		with(arguments) {
			val pathFilters = createPathFilters()
			val plugins = createPlugins()
			val classpath = createClasspath()
			val config = loadConfiguration()
			return ProcessingSettings(inputPath, config, pathFilters, parallel, disableDefaultRuleSets, plugins,
					classpath)
		}
	}
}

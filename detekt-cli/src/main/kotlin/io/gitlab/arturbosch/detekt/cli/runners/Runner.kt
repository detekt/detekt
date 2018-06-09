package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.OutputFacade
import io.gitlab.arturbosch.detekt.cli.createPathFilters
import io.gitlab.arturbosch.detekt.cli.createPlugins
import io.gitlab.arturbosch.detekt.cli.loadConfiguration
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import java.util.concurrent.ForkJoinPool
import kotlin.system.measureTimeMillis

/**
 * @author Artur Bosch
 */
class Runner(private val arguments: CliArgs) : Executable {

	override fun execute() {
		val settings = createSettings()

		val time = measureTimeMillis {
			val detektion = DetektFacade.create(settings).run()
			OutputFacade(arguments, detektion, settings).run()
		}

		println("\ndetekt finished in $time ms.")
	}

	private fun createSettings(): ProcessingSettings {
		with(arguments) {
			val pathFilters = createPathFilters()
			val plugins = createPlugins()
			val config = loadConfiguration()
			return ProcessingSettings(
					inputPath,
					config,
					pathFilters,
					parallel,
					disableDefaultRuleSets,
					plugins,
					ForkJoinPool.commonPool(),
					System.err)
		}
	}
}

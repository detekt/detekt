package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.YamlConfig
import io.gitlab.arturbosch.detekt.cli.OutputFacade
import io.gitlab.arturbosch.detekt.cli.createPathFilters
import io.gitlab.arturbosch.detekt.cli.loadConfiguration
import io.gitlab.arturbosch.detekt.cli.parseArguments
import io.gitlab.arturbosch.detekt.core.Detektor
import io.gitlab.arturbosch.detekt.core.KtTreeCompiler
import io.gitlab.arturbosch.detekt.core.ProcessingSettings

/**
 * @author Artur Bosch
 */
class Formatting {

	companion object {

		@JvmStatic
		fun main(args: Array<String>) {
			with(parseArguments(args)) {
				val config = loadConfiguration() as? YamlConfig ?:
						throw IllegalStateException("Yaml configuration with migrations rules must be provided!")
				if (debug) println(config.properties)
				val settings = ProcessingSettings(project, config, createPathFilters(), parallel, excludeDefaultRuleSets = true)
				val detektor = Detektor(settings, KtTreeCompiler.instance(settings), listOf(FormattingProvider()))
				val detektion = detektor.run()
				OutputFacade(this, config, detektion).run {
					printNotifications()
					printFindings()
				}
			}

		}
	}

}

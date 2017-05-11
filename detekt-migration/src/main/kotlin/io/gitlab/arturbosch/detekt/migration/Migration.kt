package io.gitlab.arturbosch.detekt.migration

import io.gitlab.arturbosch.detekt.api.YamlConfig
import io.gitlab.arturbosch.detekt.cli.Output
import io.gitlab.arturbosch.detekt.cli.createPathFilters
import io.gitlab.arturbosch.detekt.cli.loadConfiguration
import io.gitlab.arturbosch.detekt.cli.parseArguments
import io.gitlab.arturbosch.detekt.core.Detektor
import io.gitlab.arturbosch.detekt.core.KtTreeCompiler
import io.gitlab.arturbosch.detekt.core.ProcessingSettings

/**
 * @author Artur Bosch
 */
class Migration {

	companion object {

		@JvmStatic
		fun main(args: Array<String>) {
			with(parseArguments(args)) {
				val config = loadConfiguration() as? YamlConfig ?:
						throw IllegalStateException("Yaml configuration with migrations rules must be provided!")
				if (debug) println(config.properties)
				val settings = ProcessingSettings(project, config, createPathFilters(), parallel, excludeDefaultRuleSets = true)
				val detektor = Detektor(settings, KtTreeCompiler.instance(settings), listOf(MigrationRuleSetProvider()))
				val detektion = detektor.run()
				Output(detektion, this) // prints results
			}

		}
	}

}

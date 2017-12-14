package io.gitlab.arturbosch.detekt.migration

import io.gitlab.arturbosch.detekt.cli.OutputFacade
import io.gitlab.arturbosch.detekt.cli.createPathFilters
import io.gitlab.arturbosch.detekt.cli.loadConfiguration
import io.gitlab.arturbosch.detekt.cli.parseArguments
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.ProcessingSettings

/**
 * @author Artur Bosch
 */
class Migration {

	companion object {

		@JvmStatic
		fun main(args: Array<String>) {
			with(parseArguments(args)) {
				val config = loadConfiguration()
				val settings = ProcessingSettings(inputPath, config, createPathFilters(), parallel, true)
				val detektion = DetektFacade.create(settings, MigrationRuleSetProvider()).run()
				OutputFacade(this, detektion, settings).run()
			}

		}
	}

}

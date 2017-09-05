package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.cli.OutputFacade
import io.gitlab.arturbosch.detekt.cli.createPathFilters
import io.gitlab.arturbosch.detekt.cli.loadConfiguration
import io.gitlab.arturbosch.detekt.cli.parseArguments
import io.gitlab.arturbosch.detekt.core.Detektor
import io.gitlab.arturbosch.detekt.core.ProcessingSettings

/**
 * @author Artur Bosch
 */
class Formatting {

	companion object {

		@JvmStatic
		fun main(args: Array<String>) {
			with(parseArguments(args)) {
				val config = loadConfiguration()
				val settings = ProcessingSettings(inputPath, config, createPathFilters(), parallel, true)
				val detektor = Detektor(settings, listOf(FormattingProvider()))
				val detektion = detektor.run()
				OutputFacade(this, detektion, settings).run()
			}

		}
	}

}

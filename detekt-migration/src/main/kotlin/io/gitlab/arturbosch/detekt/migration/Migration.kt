package io.gitlab.arturbosch.detekt.migration

import io.gitlab.arturbosch.detekt.api.PROJECT
import io.gitlab.arturbosch.detekt.cli.OutputFacade
import io.gitlab.arturbosch.detekt.cli.createPathFilters
import io.gitlab.arturbosch.detekt.cli.loadConfiguration
import io.gitlab.arturbosch.detekt.cli.parseArguments
import io.gitlab.arturbosch.detekt.core.Detektor
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import org.jetbrains.kotlin.com.intellij.mock.MockProject

/**
 * @author Artur Bosch
 */
class Migration {

	companion object {

		@JvmStatic
		fun main(args: Array<String>) {
			with(parseArguments(args)) {
				makeMutable(PROJECT as MockProject)
				val config = loadConfiguration()
				val settings = ProcessingSettings(inputPath, config, createPathFilters(), parallel, true)
				val detektor = Detektor(settings, listOf(MigrationRuleSetProvider()))
				val detektion = detektor.run()
				OutputFacade(this, detektion, settings).run()
			}

		}
	}

}

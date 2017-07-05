package io.gitlab.arturbosch.detekt.sonar.sensor

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.cli.Main
import io.gitlab.arturbosch.detekt.cli.SEPARATORS
import io.gitlab.arturbosch.detekt.cli.loadConfiguration
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.Detektor
import io.gitlab.arturbosch.detekt.core.PathFilter
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.sonar.foundation.CONFIG_PATH_KEY
import io.gitlab.arturbosch.detekt.sonar.foundation.CONFIG_RESOURCE_KEY
import io.gitlab.arturbosch.detekt.sonar.foundation.LOG
import io.gitlab.arturbosch.detekt.sonar.foundation.NoAutoCorrectConfig
import io.gitlab.arturbosch.detekt.sonar.foundation.PATH_FILTERS_DEFAULTS
import io.gitlab.arturbosch.detekt.sonar.foundation.PATH_FILTERS_KEY
import io.gitlab.arturbosch.detekt.sonar.rules.DEFAULT_YAML_CONFIG
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.config.Settings
import java.io.File

/**
 * @author Artur Bosch
 */
fun configureDetektor(context: SensorContext): Detektor {
	val fileSystem = context.fileSystem()
	val baseDir = fileSystem.baseDir()
	val settings = context.settings()

	val pathFiltersString = settings.getString(PATH_FILTERS_KEY) ?: PATH_FILTERS_DEFAULTS
	val filters = pathFiltersString.split(*SEPARATORS).map { PathFilter(it) }

	val config = chooseConfig(baseDir, settings)
	val processingSettings = ProcessingSettings(
			baseDir.toPath(), NoAutoCorrectConfig(config), filters)

	return DetektFacade.instance(processingSettings)
}

private fun chooseConfig(baseDir: File, settings: Settings): Config {
	val configPath = settings.getString(CONFIG_PATH_KEY)
	val externalConfigPath = configPath?.let {
		LOG.info("Registered config path: $it")
		val configFile = File(it)
		if (!configFile.isAbsolute) { // TODO find out how to resolve always to root path, not module path
			val resolved = baseDir.resolve(configPath)
			LOG.info("Relative path detected. Resolving to project dir: $resolved")
			resolved
		} else configFile
	}

	val internalConfigResource = settings.getString(CONFIG_RESOURCE_KEY)
			?.let { if (it.isNullOrBlank()) null else it }

	val possibleParseArguments = Main().apply {
		config = externalConfigPath?.path
		configResource = internalConfigResource
	}

	val bestConfigMatch = possibleParseArguments.loadConfiguration()
	val bestConfigMatchOrDefault = bestConfigMatch.let {
		if (it == Config.empty) {
			LOG.info("No detekt yaml configuration file found, using the default configuration.")
			DEFAULT_YAML_CONFIG
		} else it
	}

	return bestConfigMatchOrDefault
}
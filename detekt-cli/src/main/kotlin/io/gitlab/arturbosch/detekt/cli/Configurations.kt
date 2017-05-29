package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.YamlConfig
import io.gitlab.arturbosch.detekt.core.PathFilter
import java.nio.file.Path
import java.nio.file.Paths
import java.io.File

/**
 * @author Artur Bosch
 */

fun Main.createPathFilters(): List<PathFilter> = filters.letIfNonEmpty { split(";", ",").map(::PathFilter) }

fun Main.createRulePaths(): List<Path> = rules.letIfNonEmpty { split(";", ",").map { Paths.get(it) } }

private fun <T> String?.letIfNonEmpty(init: String.() -> List<T>): List<T> =
		if (this == null || this.isEmpty()) listOf<T>() else this.init()

fun Main.loadConfiguration(): Config {
	return if (config != null) YamlConfig.load(config!!)
	else if (configResource != null) YamlConfig.loadResource(configResource!!)
	else if (formatting) FormatConfig(useTabs)
	else Config.empty
}

class FormatConfig(private val useTabs: Boolean) : Config {
	override fun subConfig(key: String): Config {
		return this
	}

	override fun <T : Any> valueOrDefault(key: String, default: () -> T): T {
		@Suppress("UNCHECKED_CAST")
		return when (key) {
			"autoCorrect" -> true as T
			"useTabs" -> (useTabs) as T
			else -> default()
		}
	}
}

/**
 * @author lummax
 */
class ConfigExporter(val main: Main) : Executable {

	override fun execute() {
		val defaultConfig = javaClass.getResourceAsStream("/default-detekt-config.yml") ?:
				throw IllegalStateException("Unable to load default-detekt-config.yml from resources.")
		defaultConfig.copyTo(File("default-detekt-config.yml").outputStream())
	}

}
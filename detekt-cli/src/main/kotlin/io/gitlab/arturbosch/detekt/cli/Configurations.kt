package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.CompositeConfig
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.YamlConfig
import io.gitlab.arturbosch.detekt.core.PathFilter
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */

fun Main.createPathFilters(): List<PathFilter> = filters.letIfNonEmpty { split(";", ",").map(::PathFilter) }

fun Main.createRulePaths(): List<Path> = rules.letIfNonEmpty { split(";", ",").map { Paths.get(it) } }

private fun <T> String?.letIfNonEmpty(init: String.() -> List<T>): List<T> =
		if (this == null || this.isEmpty()) listOf<T>() else this.init()

fun Main.loadConfiguration(): Config = when {
	!config.isNullOrBlank() -> parsePathConfig(config!!)
	!configResource.isNullOrBlank() -> parseResourceConfig(configResource!!)
	formatting -> FormatConfig(useTabs)
	else -> Config.empty
}.apply {
	if (debug) println(this)
}

private fun parseResourceConfig(config: String): Config {
	val urls = MultipleClasspathResourceConverter().convert(config)
	return if (urls.size == 1) {
		YamlConfig.loadResource(urls[0])
	} else {
		urls.map { YamlConfig.loadResource(it) }.reduce { composite, config -> CompositeConfig(config, composite) }
	}
}

private fun parsePathConfig(config: String): Config {
	val paths = MultipleExistingPathConverter().convert(config)
	return if (paths.size == 1) {
		YamlConfig.load(paths[0])
	} else {
		paths.map { YamlConfig.load(it) }.reduce { composite, config -> CompositeConfig(config, composite) }
	}
}

class FormatConfig(private val useTabs: Boolean) : Config {
	override fun subConfig(key: String): Config {
		return this
	}

	override fun <T : Any> valueOrDefault(key: String, default: T): T {
		@Suppress("UNCHECKED_CAST")
		return when (key) {
			"autoCorrect" -> true as T
			"useTabs" -> (useTabs) as T
			else -> default
		}
	}
}

private val DEFAULT_CONFIG = "default-detekt-config.yml"

/**
 * @author lummax
 */
class ConfigExporter(val main: Main) : Executable {

	override fun execute() {
		val defaultConfig = javaClass.getResourceAsStream("/$DEFAULT_CONFIG") ?:
				throw IllegalStateException("Unable to load $DEFAULT_CONFIG from resources.")
		defaultConfig.copyTo(File(DEFAULT_CONFIG).outputStream())
		println("\nSuccessfully copied $DEFAULT_CONFIG to project location.")
	}

}
package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.CompositeConfig
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.YamlConfig
import io.gitlab.arturbosch.detekt.core.PathFilter
import java.io.File
import java.nio.file.Path

/**
 * @author Artur Bosch
 */

fun Main.createPathFilters(): List<PathFilter> = filters.letIfNonEmpty { split(*SEPARATORS).map(::PathFilter) }

fun Main.createRulePaths(): List<Path> = rules.letIfNonEmpty {
	MultipleExistingPathConverter().convert(this)
}

private fun <T> String?.letIfNonEmpty(init: String.() -> List<T>): List<T> =
		if (this == null || this.isEmpty()) listOf<T>() else this.init()

fun Main.loadConfiguration(): Config = when {
	!config.isNullOrBlank() -> parsePathConfig(config!!)
	!configResource.isNullOrBlank() -> parseResourceConfig(configResource!!)
	formatting -> FormatConfig(useTabs)
	else -> Config.empty
}.apply {
	if (debug) println("\n$this\n")
}

private fun parseResourceConfig(configPath: String): Config {
	val urls = MultipleClasspathResourceConverter().convert(configPath)
	return if (urls.size == 1) {
		YamlConfig.loadResource(urls[0])
	} else {
		urls.map { YamlConfig.loadResource(it) }.reduce { composite, config -> CompositeConfig(config, composite) }
	}
}

private fun parsePathConfig(configPath: String): Config {
	val paths = MultipleExistingPathConverter().convert(configPath)
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
		val defaultConfig = ClasspathResourceConverter().convert(DEFAULT_CONFIG).openStream()
		defaultConfig.copyTo(File(DEFAULT_CONFIG).outputStream())
		println("\nSuccessfully copied $DEFAULT_CONFIG to project location.")
	}

}
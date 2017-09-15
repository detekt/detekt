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

fun Args.createPathFilters(): List<PathFilter> = filters.letIfNonEmpty {
	split(SEPARATOR_COMMA, SEPARATOR_SEMICOLON).map(::PathFilter)
}

fun Args.createPlugins(): List<Path> = plugins.letIfNonEmpty {
	MultipleExistingPathConverter().convert(this)
}

private fun <T> String?.letIfNonEmpty(init: String.() -> List<T>): List<T> =
		if (this == null || this.isEmpty()) listOf() else this.init()

fun Args.loadConfiguration(): Config {
	var config = when {
		!config.isNullOrBlank() -> parsePathConfig(config!!)
		!configResource.isNullOrBlank() -> parseResourceConfig(configResource!!)
		else -> loadDefaultConfig()
	}

	if (config.valueOrDefault("failFast", false)) {
		config = FailFastConfig(config, loadDefaultConfig())
	}

	if (debug) println("\n$config\n")
	return config
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

data class FailFastConfig(private val originalConfig: Config, private val defaultConfig: Config) : Config {
	override fun subConfig(key: String) = FailFastConfig(originalConfig.subConfig(key), defaultConfig.subConfig(key))

	override fun <T : Any> valueOrDefault(key: String, default: T): T {
		@Suppress("UNCHECKED_CAST")
		return when (key) {
			"active" -> originalConfig.valueOrDefault(key, true) as T
			"warningThreshold", "failThreshold" -> originalConfig.valueOrDefault(key, 0) as T
			else -> originalConfig.valueOrDefault(key, defaultConfig.valueOrDefault(key, default))
		}
	}
}

private fun loadDefaultConfig() = YamlConfig.loadResource(ClasspathResourceConverter().convert(DEFAULT_CONFIG))

private val DEFAULT_CONFIG = "default-detekt-config.yml"

/**
 * @author lummax
 */
class ConfigExporter : Executable {

	override fun execute() {
		val defaultConfig = ClasspathResourceConverter().convert(DEFAULT_CONFIG).openStream()
		defaultConfig.copyTo(File(DEFAULT_CONFIG).outputStream())
		println("\nSuccessfully copied $DEFAULT_CONFIG to project location.")
	}

}

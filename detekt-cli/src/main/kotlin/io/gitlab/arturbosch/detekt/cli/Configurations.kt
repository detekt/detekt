package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.CompositeConfig
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.YamlConfig
import io.gitlab.arturbosch.detekt.core.PathFilter
import java.nio.file.Path

/**
 * @author Artur Bosch
 */

fun CliArgs.createPathFilters(): List<PathFilter> = filters.letIfNonEmpty {
	split(SEPARATOR_COMMA, SEPARATOR_SEMICOLON).map(::PathFilter)
}

fun CliArgs.createPlugins(): List<Path> = plugins.letIfNonEmpty {
	MultipleExistingPathConverter().convert(this)
}

private fun <T> String?.letIfNonEmpty(init: String.() -> List<T>): List<T> =
		if (this == null || this.isEmpty()) listOf() else this.init()

fun CliArgs.loadConfiguration(): Config {
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

private fun loadDefaultConfig() = YamlConfig.loadResource(ClasspathResourceConverter().convert(DEFAULT_CONFIG))

const val DEFAULT_CONFIG = "default-detekt-config.yml"

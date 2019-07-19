package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.CompositeConfig
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.YamlConfig
import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import java.nio.file.Path

fun CliArgs.createFilters(): PathFilters? = PathFilters.of(includes, excludes)

fun CliArgs.createPlugins(): List<Path> = plugins.letIfNonEmpty {
    MultipleExistingPathConverter().convert(this)
}

fun CliArgs.createClasspath(): List<String> = classpath.letIfNonEmpty { split(";") }

private fun <T> String?.letIfNonEmpty(init: String.() -> List<T>): List<T> =
    if (this == null || this.isEmpty()) listOf() else this.init()

fun CliArgs.loadConfiguration(): Config {
    var declaredConfig: Config? = when {
        !config.isNullOrBlank() -> parsePathConfig(config!!)
        !configResource.isNullOrBlank() -> parseResourceConfig(configResource!!)
        else -> null
    }
    var defaultConfig: Config? = null

    if (buildUponDefaultConfig) {
        defaultConfig = loadDefaultConfig()
        declaredConfig = CompositeConfig(declaredConfig ?: defaultConfig, defaultConfig)
    }

    if (failFast) {
        val initializedDefaultConfig = defaultConfig ?: loadDefaultConfig()
        declaredConfig = FailFastConfig(declaredConfig ?: initializedDefaultConfig, initializedDefaultConfig)
    }

    if (debug) println("\n$declaredConfig\n")
    return declaredConfig ?: loadDefaultConfig()
}

private fun parseResourceConfig(configPath: String): Config {
    val urls = MultipleClasspathResourceConverter().convert(configPath)
    return if (urls.size == 1) {
        YamlConfig.loadResource(urls[0])
    } else {
        urls.asSequence()
            .map { YamlConfig.loadResource(it) }
            .reduce { composite, config -> CompositeConfig(config, composite) }
    }
}

private fun parsePathConfig(configPath: String): Config {
    val paths = MultipleExistingPathConverter().convert(configPath)
    return if (paths.size == 1) {
        YamlConfig.load(paths[0])
    } else {
        paths.asSequence()
            .map { YamlConfig.load(it) }
            .reduce { composite, config -> CompositeConfig(config, composite) }
    }
}

private fun loadDefaultConfig() = YamlConfig.loadResource(ClasspathResourceConverter().convert(DEFAULT_CONFIG))

const val DEFAULT_CONFIG = "default-detekt-config.yml"

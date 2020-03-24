package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.CompositeConfig
import io.gitlab.arturbosch.detekt.api.internal.DisabledAutoCorrectConfig
import io.gitlab.arturbosch.detekt.api.internal.FailFastConfig
import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import io.gitlab.arturbosch.detekt.api.internal.YamlConfig
import java.net.URI
import java.net.URL
import java.nio.file.FileSystemNotFoundException
import java.nio.file.FileSystems
import java.nio.file.Path

fun CliArgs.createFilters(): PathFilters? = PathFilters.of(includes, excludes)

fun CliArgs.createPlugins(): List<Path> = plugins.letIfNonEmpty {
    MultipleExistingPathConverter().convert(this)
}

fun CliArgs.createClasspath(): List<String> = classpath.letIfNonEmpty { split(";") }

private fun <T> String?.letIfNonEmpty(init: String.() -> List<T>): List<T> =
    if (this == null || this.isEmpty()) listOf() else this.init()

@Suppress("UnsafeCallOnNullableType")
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
        declaredConfig = FailFastConfig(declaredConfig
            ?: initializedDefaultConfig, initializedDefaultConfig)
    }

    if (!autoCorrect) {
        declaredConfig = DisabledAutoCorrectConfig(declaredConfig ?: loadDefaultConfig())
    }

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

const val DEFAULT_CONFIG = "default-detekt-config.yml"

fun loadDefaultConfig() = YamlConfig.loadResource(ClasspathResourceConverter().convert(DEFAULT_CONFIG))

private fun initFileSystem(uri: URI) {
    runCatching {
        try {
            FileSystems.getFileSystem(uri)
        } catch (e: FileSystemNotFoundException) {
            FileSystems.newFileSystem(uri, mapOf("create" to "true"))
        }
    }
}

fun CliArgs.extractUris(): Collection<URI> {
    val pathUris = config?.let { MultipleExistingPathConverter().convert(it).map(Path::toUri) } ?: emptyList()
    val resourceUris = configResource?.let { MultipleClasspathResourceConverter().convert(it).map(URL::toURI) }
        ?: emptyList()
    resourceUris.forEach(::initFileSystem)
    return resourceUris + pathUris
}

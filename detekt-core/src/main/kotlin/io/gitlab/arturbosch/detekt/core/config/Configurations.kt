package io.gitlab.arturbosch.detekt.core.config

import io.github.detekt.tooling.api.spec.ConfigSpec
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.github.detekt.utils.openSafeStream
import io.gitlab.arturbosch.detekt.api.Config
import java.net.URI
import java.net.URL
import java.nio.file.FileSystemNotFoundException
import java.nio.file.FileSystems
import java.nio.file.Path

internal fun ProcessingSpec.loadConfiguration(): Config = with(configSpec) {
    return when {
        configPaths.isNotEmpty() -> parsePathConfig(configPaths)
        resources.isNotEmpty() -> parseResourceConfig(resources)
        else -> Config.empty
    }
}

private fun parseResourceConfig(urls: Collection<URL>): Config =
    if (urls.size == 1) {
        urls.single().openSafeStream().reader().use(YamlConfig::load)
    } else {
        urls.asSequence()
            .map { it.openSafeStream().reader().use(YamlConfig::load) }
            .reduce { composite, config -> CompositeConfig(config, composite) }
    }

private fun parsePathConfig(paths: Collection<Path>): Config =
    if (paths.size == 1) {
        YamlConfig.load(paths.single())
    } else {
        paths.asSequence()
            .map { YamlConfig.load(it) }
            .reduce { composite, config -> CompositeConfig(config, composite) }
    }

internal fun ConfigSpec.extractUris(): Collection<URI> {
    fun initFileSystem(uri: URI) {
        runCatching {
            @Suppress("SwallowedException") // Create file system inferred from URI if it does not exist.
            try {
                FileSystems.getFileSystem(uri)
            } catch (e: FileSystemNotFoundException) {
                FileSystems.newFileSystem(uri, mapOf("create" to "true"))
            }
        }
    }

    val pathUris = configPaths.map(Path::toUri)
    val resourceUris = resources.map(URL::toURI)
    resourceUris.forEach(::initFileSystem)
    return resourceUris + pathUris
}

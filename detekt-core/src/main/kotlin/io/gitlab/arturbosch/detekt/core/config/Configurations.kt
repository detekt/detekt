package io.gitlab.arturbosch.detekt.core.config

import io.github.detekt.tooling.api.spec.ConfigSpec
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.CompositeConfig
import io.gitlab.arturbosch.detekt.api.internal.YamlConfig
import java.net.URI
import java.net.URL
import java.nio.file.FileSystemNotFoundException
import java.nio.file.FileSystems
import java.nio.file.Path

internal fun ProcessingSpec.loadConfiguration(): Config = with(configSpec) {
    var declaredConfig: Config? = when {
        configPaths.isNotEmpty() -> parsePathConfig(configPaths)
        resources.isNotEmpty() -> parseResourceConfig(resources)
        else -> null
    }

    if (useDefaultConfig) {
        declaredConfig = if (declaredConfig == null) {
            DefaultConfig.newInstance()
        } else {
            CompositeConfig(declaredConfig, DefaultConfig.newInstance())
        }
    }

    return declaredConfig ?: DefaultConfig.newInstance()
}

private fun parseResourceConfig(urls: Collection<URL>): Config =
    if (urls.size == 1) {
        YamlConfig.loadResource(urls.first())
    } else {
        urls.asSequence()
            .map { YamlConfig.loadResource(it) }
            .reduce { composite, config -> CompositeConfig(config, composite) }
    }

private fun parsePathConfig(paths: Collection<Path>): Config =
    if (paths.size == 1) {
        YamlConfig.load(paths.first())
    } else {
        paths.asSequence()
            .map { YamlConfig.load(it) }
            .reduce { composite, config -> CompositeConfig(config, composite) }
    }

internal fun ConfigSpec.extractUris(): Collection<URI> {
    fun initFileSystem(uri: URI) {
        runCatching {
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

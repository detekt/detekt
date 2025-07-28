package io.gitlab.arturbosch.detekt.core.tooling

import dev.detekt.tooling.api.DefaultConfigurationProvider
import dev.detekt.tooling.api.spec.ExtensionsSpec
import dev.detekt.tooling.api.spec.ProcessingSpec
import dev.detekt.utils.getSafeResourceAsStream
import dev.detekt.utils.openSafeStream
import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.core.config.YamlConfig
import io.gitlab.arturbosch.detekt.core.settings.ExtensionFacade
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class DefaultConfigProvider : DefaultConfigurationProvider {
    private lateinit var extensionsSpec: ExtensionsSpec

    override fun init(extensionsSpec: ExtensionsSpec) {
        this.extensionsSpec = extensionsSpec
    }

    override fun get(): Config = extensionsSpec.getDefaultConfiguration()

    override fun copy(targetLocation: Path) {
        Files.copy(configInputStream(extensionsSpec), targetLocation, StandardCopyOption.REPLACE_EXISTING)
    }
}

private fun configInputStream(extensionsSpec: ExtensionsSpec): InputStream {
    val outputStream = ByteArrayOutputStream()

    requireNotNull(DefaultConfigProvider::class.java.getSafeResourceAsStream("/default-detekt-config.yml"))
        .use { it.copyTo(outputStream) }

    ExtensionFacade(extensionsSpec.plugins).pluginLoader
        .getSafeResourcesAsStreams("config/config.yml")
        .forEach { inputStream ->
            outputStream.write('\n'.code)
            inputStream.use { it.copyTo(outputStream) }
        }

    return ByteArrayInputStream(outputStream.toByteArray())
}

private fun ClassLoader.getSafeResourcesAsStreams(name: String): Sequence<InputStream> =
    getResources(name)
        .asSequence()
        .map { it.openSafeStream() }

private fun ExtensionsSpec.getDefaultConfiguration(): Config = YamlConfig.load(configInputStream(this).reader())

fun ProcessingSpec.getDefaultConfiguration(): Config = extensionsSpec.getDefaultConfiguration()

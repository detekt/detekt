package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.tooling.api.DefaultConfigurationProvider
import io.github.detekt.tooling.api.spec.ExtensionsSpec
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.github.detekt.utils.getSafeResourceAsStream
import io.github.detekt.utils.openSafeStream
import io.gitlab.arturbosch.detekt.api.Config
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

    requireNotNull(extensionsSpec.javaClass.getSafeResourceAsStream("/default-detekt-config.yml"))
        .use { it.copyTo(outputStream) }

    ExtensionFacade(extensionsSpec.plugins).pluginLoader
        .getResourcesAsStream("config/config.yml")
        .forEach { inputStream ->
            outputStream.bufferedWriter().append('\n').flush()
            inputStream.use { it.copyTo(outputStream) }
        }

    return ByteArrayInputStream(outputStream.toByteArray())
}

private fun ClassLoader.getResourcesAsStream(name: String): Sequence<InputStream> {
    return getResources(name)
        .asSequence()
        .map { it.openSafeStream() }
}

private fun ExtensionsSpec.getDefaultConfiguration(): Config {
    return YamlConfig.load(configInputStream(this).reader())
}

fun ProcessingSpec.getDefaultConfiguration(): Config {
    return extensionsSpec.getDefaultConfiguration()
}

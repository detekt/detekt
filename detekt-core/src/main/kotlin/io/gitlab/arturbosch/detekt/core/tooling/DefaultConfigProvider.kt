package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.tooling.api.DefaultConfigurationProvider
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.github.detekt.utils.getSafeResourceAsStream
import io.github.detekt.utils.openSafeStream
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.core.config.YamlConfig
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class DefaultConfigProvider : DefaultConfigurationProvider {
    private lateinit var spec: ProcessingSpec

    override fun init(spec: ProcessingSpec) {
        this.spec = spec
    }

    override fun get(): Config = spec.getDefaultConfiguration()

    override fun copy(targetLocation: Path) {
        val configUrl = javaClass.getResource(DEFAULT_CONFIG_RESOURCES_PATH)!!
        Files.copy(configUrl.openSafeStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING)
    }
}

fun ProcessingSpec.getDefaultConfiguration(): Config {
    return requireNotNull(javaClass.getSafeResourceAsStream(DEFAULT_CONFIG_RESOURCES_PATH))
        .reader()
        .use(YamlConfig::load)
}

private const val DEFAULT_CONFIG_RESOURCES_PATH = "/default-detekt-config.yml"

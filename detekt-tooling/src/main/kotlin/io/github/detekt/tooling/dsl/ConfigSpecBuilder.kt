package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.ConfigSpec
import java.net.URL
import java.nio.file.Path

@ProcessingModelDsl
class ConfigSpecBuilder : Builder<ConfigSpec>, ConfigSpec {

    override var shouldValidateBeforeAnalysis: Boolean = true
    override var knownPatterns: Collection<String> = emptyList()
    override var useDefaultConfig: Boolean = false // false to be backwards compatible in 1.X
    override var resources: Collection<URL> = emptyList()
    override var configPaths: Collection<Path> = emptyList()

    override fun build(): ConfigSpec = ConfigModel(
        shouldValidateBeforeAnalysis,
        knownPatterns,
        useDefaultConfig,
        resources,
        configPaths
    )
}

internal data class ConfigModel(
    override val shouldValidateBeforeAnalysis: Boolean,
    override val knownPatterns: Collection<String>,
    override val useDefaultConfig: Boolean,
    override val resources: Collection<URL>,
    override val configPaths: Collection<Path>
) : ConfigSpec

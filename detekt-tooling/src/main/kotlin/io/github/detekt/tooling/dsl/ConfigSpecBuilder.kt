package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.ConfigSpec
import java.net.URL
import java.nio.file.Path

@ProcessingModelDsl
class ConfigSpecBuilder : Builder<ConfigSpec> {

    var shouldValidateBeforeAnalysis: Boolean = true
    var knownPatterns: Collection<String> = emptyList()
    var useDefaultConfig: Boolean = false // false to be backwards compatible in 1.X
    var resources: Collection<URL> = emptyList()
    var configPaths: Collection<Path> = emptyList()

    override fun build(): ConfigSpec = ConfigModel(
        shouldValidateBeforeAnalysis,
        knownPatterns,
        useDefaultConfig,
        resources,
        configPaths
    )
}

private data class ConfigModel(
    override val shouldValidateBeforeAnalysis: Boolean,
    override val knownPatterns: Collection<String>,
    override val useDefaultConfig: Boolean,
    override val resources: Collection<URL>,
    override val configPaths: Collection<Path>
) : ConfigSpec

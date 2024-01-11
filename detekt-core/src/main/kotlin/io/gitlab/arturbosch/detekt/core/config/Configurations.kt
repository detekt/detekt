package io.gitlab.arturbosch.detekt.core.config

import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.github.detekt.utils.openSafeStream
import io.gitlab.arturbosch.detekt.api.Config

internal fun ProcessingSpec.loadConfiguration(): Config = with(configSpec) {
    return when {
        configPaths.isNotEmpty() -> configPaths.map { YamlConfig.load(it) }
        resources.isNotEmpty() -> resources.map { it.openSafeStream().reader().use(YamlConfig::load) }
        else -> listOf(Config.empty)
    }
        .reduce { composite, config -> CompositeConfig(config, composite) }
}

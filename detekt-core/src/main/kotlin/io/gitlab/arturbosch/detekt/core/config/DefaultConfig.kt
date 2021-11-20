package io.gitlab.arturbosch.detekt.core.config

import io.github.detekt.tooling.internal.getSafeResourceAsStream
import io.gitlab.arturbosch.detekt.api.Config

internal object DefaultConfig {

    const val RESOURCE_NAME = "default-detekt-config.yml"

    fun newInstance(): Config {
        val configReader = checkNotNull(javaClass.getSafeResourceAsStream("/$RESOURCE_NAME")).reader()
        return YamlConfig.load(configReader)
    }
}

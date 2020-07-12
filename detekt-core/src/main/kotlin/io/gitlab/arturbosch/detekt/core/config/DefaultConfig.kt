package io.gitlab.arturbosch.detekt.core.config

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.YamlConfig

internal object DefaultConfig {

    const val RESOURCE_NAME = "default-detekt-config.yml"

    fun newInstance(): Config {
        val configUrl = javaClass.getResource("/$RESOURCE_NAME")
        return YamlConfig.loadResource(configUrl)
    }
}

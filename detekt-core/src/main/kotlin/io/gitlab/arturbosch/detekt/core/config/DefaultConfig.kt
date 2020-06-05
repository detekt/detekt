package io.gitlab.arturbosch.detekt.core.config

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.YamlConfig

object DefaultConfig {

    const val RESOURCE_NAME = "default-detekt-config.yml"

    fun newInstance(): Config {
        val configUrl = InvalidConfig::class.java.getResource("/$RESOURCE_NAME")
        return YamlConfig.loadResource(configUrl)
    }
}

package io.gitlab.arturbosch.detekt.core.config

import io.github.detekt.utils.getSafeResourceAsStream
import io.gitlab.arturbosch.detekt.api.Config

internal object DefaultConfig {

    const val RESOURCE_NAME = "default-detekt-config.yml"

    fun newInstance(): Config {
        return checkNotNull(javaClass.getSafeResourceAsStream("/$RESOURCE_NAME"))
            .reader()
            .use(YamlConfig::load)
    }
}

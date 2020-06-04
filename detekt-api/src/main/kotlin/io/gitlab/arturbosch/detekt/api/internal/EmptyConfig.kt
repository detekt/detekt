package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.Config

/**
 * NOP-implementation of a config object.
 */
internal object EmptyConfig : Config {

    override fun subConfig(key: String): EmptyConfig = this

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> valueOrNull(key: String): T? = when (key) {
        "active" -> true as? T
        else -> null
    }
}

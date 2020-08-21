package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.Config

@Suppress("UNCHECKED_CAST")
data class AllActiveConfig(
    private val originalConfig: Config
) : Config, ValidatableConfiguration {

    override fun subConfig(key: String) = AllActiveConfig(originalConfig.subConfig(key))

    override fun <T : Any> valueOrDefault(key: String, default: T): T {
        return when (key) {
            "active" -> originalConfig.valueOrDefault(key, true) as T
            else -> originalConfig.valueOrDefault(key, default)
        }
    }

    override fun <T : Any> valueOrNull(key: String): T? {
        return when (key) {
            "active" -> originalConfig.valueOrNull(key) ?: true as? T
            else -> originalConfig.valueOrNull(key)
        }
    }

    override fun validate(baseline: Config, excludePatterns: Set<Regex>) =
        validateConfig(originalConfig, baseline, excludePatterns)
}

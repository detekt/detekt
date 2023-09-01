package io.gitlab.arturbosch.detekt.core.config

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.core.config.validation.ValidatableConfiguration
import io.gitlab.arturbosch.detekt.core.config.validation.validateConfig

@Suppress("UNCHECKED_CAST")
internal data class AllRulesConfig(
    private val originalConfig: Config,
    private val defaultConfig: Config,
    private val deprecatedRuleIds: Set<String> = emptySet()
) : Config, ValidatableConfiguration {

    override val parentPath: String?
        get() = originalConfig.parentPath ?: defaultConfig.parentPath

    override fun subConfig(key: String) =
        AllRulesConfig(
            originalConfig = originalConfig.subConfig(key),
            defaultConfig = defaultConfig.subConfig(key),
            deprecatedRuleIds = deprecatedRuleIds
        )

    override fun <T : Any> valueOrDefault(key: String, default: T): T {
        return when (key) {
            Config.ACTIVE_KEY -> if (isDeprecated()) false as T else originalConfig.valueOrDefault(key, true) as T
            else -> originalConfig.valueOrDefault(key, defaultConfig.valueOrDefault(key, default))
        }
    }

    override fun <T : Any> valueOrNull(key: String): T? {
        return when (key) {
            Config.ACTIVE_KEY -> if (isDeprecated()) false as T else originalConfig.valueOrNull(key) ?: true as? T
            else -> originalConfig.valueOrNull(key) ?: defaultConfig.valueOrNull(key)
        }
    }

    override fun validate(baseline: Config, excludePatterns: Set<Regex>) =
        validateConfig(originalConfig, baseline, excludePatterns)

    private fun isDeprecated(): Boolean {
        return parentPath?.let { deprecatedRuleIds.contains(it) } ?: false
    }
}

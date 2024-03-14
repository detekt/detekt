package io.gitlab.arturbosch.detekt.core.config

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.core.config.validation.DeprecatedRule
import io.gitlab.arturbosch.detekt.core.config.validation.ValidatableConfiguration
import io.gitlab.arturbosch.detekt.core.config.validation.validateConfig

@Suppress("UNCHECKED_CAST")
internal data class AllRulesConfig(
    private val wrapped: Config,
    private val deprecatedRules: Set<DeprecatedRule>,
    override val parent: Config? = null,
) : Config, ValidatableConfiguration {

    override val parentPath: String?
        get() = wrapped.parentPath

    override fun subConfig(key: String) =
        AllRulesConfig(wrapped.subConfig(key), deprecatedRules, this)

    override fun subConfigKeys(): Set<String> {
        return wrapped.subConfigKeys()
    }

    override fun <T : Any> valueOrDefault(key: String, default: T): T {
        return when (key) {
            Config.ACTIVE_KEY -> if (isDeprecated()) false as T else wrapped.valueOrDefault(key, true) as T
            else -> wrapped.valueOrDefault(key, default)
        }
    }

    override fun <T : Any> valueOrNull(key: String): T? {
        return when (key) {
            Config.ACTIVE_KEY -> if (isDeprecated()) false as T else wrapped.valueOrNull(key) ?: true as? T
            else -> wrapped.valueOrNull(key)
        }
    }

    override fun validate(baseline: Config, excludePatterns: Set<Regex>) =
        validateConfig(wrapped, baseline, excludePatterns)

    private fun isDeprecated(): Boolean = deprecatedRules.any { parentPath == it.toPath() }

    private fun DeprecatedRule.toPath() = "$ruleSetId > $ruleId"
}

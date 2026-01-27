package dev.detekt.core.config

import dev.detekt.api.Config
import dev.detekt.core.config.validation.DeprecatedRule
import dev.detekt.core.config.validation.ValidatableConfiguration
import dev.detekt.core.config.validation.validateConfig
import dev.detekt.core.util.indentCompat

@Suppress("UNCHECKED_CAST")
internal data class AllRulesConfig(
    private val wrapped: Config,
    private val deprecatedRules: Set<DeprecatedRule>,
    override val parent: Config? = null,
) : Config,
    ValidatableConfiguration {

    override val key: String?
        get() = wrapped.key

    override fun subConfig(key: String) = AllRulesConfig(wrapped.subConfig(key), deprecatedRules, this)

    override fun subConfigKeys(): Set<String> = wrapped.subConfigKeys()

    override fun <T : Any> valueOrDefault(key: String, default: T): T =
        when (key) {
            Config.ACTIVE_KEY -> if (isDeprecated()) false as T else wrapped.valueOrDefault(key, true) as T
            else -> wrapped.valueOrDefault(key, default)
        }

    override fun <T : Any> valueOrNull(key: String): T? =
        when (key) {
            Config.ACTIVE_KEY -> if (isDeprecated()) false as T else wrapped.valueOrNull(key) ?: true as? T
            else -> wrapped.valueOrNull(key)
        }

    override fun validate(baseline: Config, excludePatterns: Set<Regex>) =
        validateConfig(wrapped, baseline, excludePatterns)

    private fun isDeprecated(): Boolean = deprecatedRules.any { key == it.ruleName && parent?.key == it.ruleSetId }

    @Suppress("MagicNumber")
    override fun toString() =
        """
            AllRulesConfig(
                wrapped=${wrapped.toString().indentCompat(12).trim()},
                deprecatedRules=$deprecatedRules,
            )
        """.trimIndent()
}

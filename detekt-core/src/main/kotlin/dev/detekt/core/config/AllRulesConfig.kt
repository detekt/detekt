package dev.detekt.core.config

import dev.detekt.api.Config
import dev.detekt.core.config.validation.DeprecatedRule
import dev.detekt.core.config.validation.ValidatableConfiguration
import dev.detekt.core.config.validation.validateConfig
import dev.detekt.core.util.indentCompat
import kotlin.reflect.KClass
import kotlin.reflect.cast

internal data class AllRulesConfig(
    private val wrapped: Config,
    private val deprecatedRules: Set<DeprecatedRule>,
    override val parent: Config? = null,
    private val key: String? = null,
) : Config,
    ValidatableConfiguration {
    override fun subConfig(key: String) = AllRulesConfig(wrapped.subConfig(key), deprecatedRules, this, key)

    override fun subConfigKeys(): Set<String> = wrapped.subConfigKeys()

    override fun <T : Any> valueOrNull(key: String, type: KClass<T>): T? =
        when (key) {
            Config.ACTIVE_KEY -> type.cast(if (isDeprecated()) false else wrapped.valueOrNull(key, type) ?: true)
            else -> wrapped.valueOrNull(key, type)
        }

    override fun validate(baseline: Config, excludePatterns: Set<Regex>) =
        validateConfig(wrapped, baseline, excludePatterns)

    private fun isDeprecated(): Boolean =
        deprecatedRules.any { key == it.ruleName && (parent as? AllRulesConfig)?.key == it.ruleSetId }

    @Suppress("MagicNumber")
    override fun toString() =
        """
            AllRulesConfig(
                wrapped=${wrapped.toString().indentCompat(12).trim()},
                deprecatedRules=$deprecatedRules,
            )
        """.trimIndent()
}

package dev.detekt.core.config

import dev.detekt.api.Config
import dev.detekt.api.Notification
import dev.detekt.core.config.validation.ValidatableConfiguration
import dev.detekt.core.config.validation.validateConfig
import dev.detekt.core.util.indentCompat
import kotlin.reflect.KClass
import kotlin.reflect.cast

class DisabledAutoCorrectConfig(private val wrapped: Config, override val parent: Config? = null) :
    Config,
    ValidatableConfiguration {

    override fun subConfig(key: String): Config = DisabledAutoCorrectConfig(wrapped.subConfig(key), this)

    override fun subConfigKeys(): Set<String> = wrapped.subConfigKeys()

    override fun <T : Any> valueOrNull(key: String, type: KClass<T>): T? =
        when (key) {
            Config.AUTO_CORRECT_KEY -> type.cast(false)
            else -> wrapped.valueOrNull(key, type)
        }

    override fun validate(baseline: Config, excludePatterns: Set<Regex>): List<Notification> =
        validateConfig(wrapped, baseline, excludePatterns)

    @Suppress("MagicNumber")
    override fun toString() =
        """
            DisabledAutoCorrectConfig(
                ${wrapped.toString().indentCompat(12).trim()},
            )
        """.trimIndent()
}

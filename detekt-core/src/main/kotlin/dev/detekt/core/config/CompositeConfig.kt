package dev.detekt.core.config

import dev.detekt.api.Config
import dev.detekt.api.Notification
import dev.detekt.core.config.validation.ValidatableConfiguration
import dev.detekt.core.config.validation.validateConfig
import dev.detekt.core.util.indentCompat

/**
 * Wraps two different configuration which should be considered when retrieving properties.
 */
class CompositeConfig(
    private val lookFirst: Config,
    private val lookSecond: Config,
    override val parent: Config? = null,
) : Config, ValidatableConfiguration {

    override val parentPath: String?
        get() = lookFirst.parentPath ?: lookSecond.parentPath

    override fun subConfig(key: String): Config =
        CompositeConfig(lookFirst.subConfig(key), lookSecond.subConfig(key), this)

    override fun subConfigKeys(): Set<String> = lookFirst.subConfigKeys() + lookSecond.subConfigKeys()

    override fun <T : Any> valueOrDefault(key: String, default: T): T {
        if (lookFirst.valueOrNull<T>(key) != null) {
            return lookFirst.valueOrDefault(key, default)
        }
        return lookSecond.valueOrDefault(key, default)
    }

    override fun <T : Any> valueOrNull(key: String): T? =
        lookFirst.valueOrNull(key) ?: lookSecond.valueOrNull(key)

    @Suppress("MagicNumber")
    override fun toString() = """
        CompositeConfig(
            lookFirst=${lookFirst.toString().indentCompat(12).trim()},
            lookSecond=${lookSecond.toString().indentCompat(12).trim()},
        )
    """.trimIndent()

    /**
     * Validates both sides of the composite config according to defined properties of the baseline config.
     */
    override fun validate(baseline: Config, excludePatterns: Set<Regex>): List<Notification> =
        validateConfig(lookFirst, baseline, excludePatterns) + validateConfig(lookSecond, baseline, excludePatterns)
}

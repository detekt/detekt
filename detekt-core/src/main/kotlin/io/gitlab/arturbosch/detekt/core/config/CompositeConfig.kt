package io.gitlab.arturbosch.detekt.core.config

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.core.config.validation.ValidatableConfiguration
import io.gitlab.arturbosch.detekt.core.config.validation.validateConfig

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

    override fun <T : Any> valueOrDefault(key: String, default: T): T {
        if (lookFirst.valueOrNull<T>(key) != null) {
            return lookFirst.valueOrDefault(key, default)
        }
        return lookSecond.valueOrDefault(key, default)
    }

    override fun <T : Any> valueOrNull(key: String): T? =
        lookFirst.valueOrNull(key) ?: lookSecond.valueOrNull(key)

    override fun toString(): String = "CompositeConfig(lookFirst=$lookFirst, lookSecond=$lookSecond)"

    /**
     * Validates both sides of the composite config according to defined properties of the baseline config.
     */
    override fun validate(baseline: Config, excludePatterns: Set<Regex>): List<Notification> =
        validateConfig(lookFirst, baseline, excludePatterns) + validateConfig(lookSecond, baseline, excludePatterns)
}

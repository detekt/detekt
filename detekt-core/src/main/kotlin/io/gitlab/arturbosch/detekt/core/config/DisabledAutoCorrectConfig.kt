package io.gitlab.arturbosch.detekt.core.config

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.core.config.validation.ValidatableConfiguration
import io.gitlab.arturbosch.detekt.core.config.validation.validateConfig
import io.gitlab.arturbosch.detekt.core.util.indentCompat

@Suppress("UNCHECKED_CAST")
class DisabledAutoCorrectConfig(
    private val wrapped: Config,
    override val parent: Config? = null,
) : Config, ValidatableConfiguration {

    override val parentPath: String?
        get() = wrapped.parentPath

    override fun subConfig(key: String): Config = DisabledAutoCorrectConfig(wrapped.subConfig(key), this)

    override fun subConfigKeys(): Set<String> = wrapped.subConfigKeys()

    override fun <T : Any> valueOrDefault(key: String, default: T): T = when (key) {
        Config.AUTO_CORRECT_KEY -> false as T
        else -> wrapped.valueOrDefault(key, default)
    }

    override fun <T : Any> valueOrNull(key: String): T? = when (key) {
        Config.AUTO_CORRECT_KEY -> false as T
        else -> wrapped.valueOrNull(key)
    }

    override fun validate(baseline: Config, excludePatterns: Set<Regex>): List<Notification> =
        validateConfig(wrapped, baseline, excludePatterns)

    @Suppress("MagicNumber")
    override fun toString() = """
        DisabledAutoCorrectConfig(
            ${wrapped.toString().indentCompat(12).trim()},
        )
    """.trimIndent()
}

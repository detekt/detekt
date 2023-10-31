package io.gitlab.arturbosch.detekt.core.config.validation

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ConfigValidator
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.commaSeparatedPattern
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.rules.RuleSetLocator

internal class DefaultPropertiesConfigValidator(
    private val settings: ProcessingSettings,
    private val baseline: Config,
) : ConfigValidator {

    override val id: String = "DefaultPropertiesConfigValidator"

    override fun validate(config: Config): Collection<Notification> {
        fun patterns(): Set<Regex> {
            val pluginExcludes = RuleSetLocator(settings).load()
                .filter { it !is DefaultRuleSetProvider }
                .map { "${it.ruleSetId}.*".toRegex() }
            val configExcludes = config.subConfig("config").valueOrDefault("excludes", "")
                .commaSeparatedPattern(",")
                .map { it.toRegex() }
            return buildSet {
                addAll(pluginExcludes)
                addAll(DEFAULT_PROPERTY_EXCLUDES)
                addAll(configExcludes)
            }
        }
        return validateConfig(config, baseline, patterns())
    }
}

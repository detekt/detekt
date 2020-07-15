package io.gitlab.arturbosch.detekt.core.config

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ConfigValidator
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.internal.CommaSeparatedPattern
import io.gitlab.arturbosch.detekt.api.internal.DEFAULT_PROPERTY_EXCLUDES
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.validateConfig
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.rules.RuleSetLocator

class DefaultPropertiesConfigValidator(
    private val settings: ProcessingSettings
) : ConfigValidator {

    override fun validate(config: Config): Collection<Notification> {
        fun patterns(): Set<Regex> {
            val pluginExcludes = RuleSetLocator(settings).load()
                .filter { it !is DefaultRuleSetProvider }
                .joinToString(",") { "${it.ruleSetId}.*" }
            val configExcludes = config.subConfig("config").valueOrDefault("excludes", "")
            val allExcludes = "$configExcludes,$DEFAULT_PROPERTY_EXCLUDES,$pluginExcludes"
            return CommaSeparatedPattern(allExcludes).mapToRegex()
        }
        return validateConfig(config, DefaultConfig.newInstance(), patterns())
    }
}

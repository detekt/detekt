package io.gitlab.arturbosch.detekt.core.config.validation

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.SimpleNotification
import io.gitlab.arturbosch.detekt.core.config.YamlConfig
import java.util.ServiceLoader

internal class MissingRulesConfigValidator(
    private val baseline: YamlConfig,
    excludePatterns: Set<Regex>,
) : AbstractYamlConfigValidator(excludePatterns) {

    override fun validate(
        configToValidate: YamlConfig,
        settings: ValidationSettings
    ): Collection<Notification> {
        if (!settings.checkExhaustiveness) {
            return emptyList()
        }
        return defaultRuleSetNames.flatMap { ruleSet -> validateRuleSet(ruleSet, configToValidate, settings) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun validateRuleSet(
        ruleSet: String,
        configToValidate: YamlConfig,
        settings: ValidationSettings
    ): List<Notification> {
        val configPropertiesToValidate = configToValidate.properties
        val ruleSetConfigToValidate = configPropertiesToValidate[ruleSet] as? Map<String, Any>
        val ruleSetConfigFromBaseline = baseline.properties[ruleSet] as? Map<String, Any>
        return when {
            ruleSetConfigFromBaseline == null -> emptyList()
            ruleSetConfigToValidate == null -> listOf(ruleSetMissing(ruleSet, settings))
            else -> checkForMissingRules(ruleSet, ruleSetConfigToValidate, ruleSetConfigFromBaseline, settings)
        }
    }

    private fun checkForMissingRules(
        ruleSetName: String,
        ruleSetConfigToValidate: Map<String, Any>,
        ruleSetConfigFromBaseline: Map<String, Any>?,
        settings: ValidationSettings
    ): List<Notification> {
        if (ruleSetConfigFromBaseline == null) {
            return emptyList()
        }
        if (ruleSetConfigToValidate[Config.ACTIVE_KEY] == false) {
            return emptyList()
        }

        return ruleSetConfigFromBaseline.keys
            .filter { ruleNameCandidate ->
                settings.excludePatterns.none { it.matches("$ruleSetName>$ruleNameCandidate") }
            }
            .filter { ruleName -> !ruleSetConfigToValidate.containsKey(ruleName) }
            .map { ruleName -> ruleMissing(ruleName, ruleSetName, settings) }
    }

    private fun ruleMissing(
        ruleName: String,
        ruleSetName: String,
        settings: ValidationSettings,
    ): Notification =
        SimpleNotification(
            "Rule '$ruleName' from the '$ruleSetName' rule set is missing in the configuration.",
            if (settings.warningsAsErrors) Notification.Level.Error else Notification.Level.Warning,
        )

    private fun ruleSetMissing(
        ruleSetName: String,
        settings: ValidationSettings,
    ): Notification =
        SimpleNotification(
            "Rule set '$ruleSetName' is missing in the configuration.",
            if (settings.warningsAsErrors) Notification.Level.Error else Notification.Level.Warning,
        )

    companion object {
        private val defaultRuleSetNames: List<String> by lazy(Companion::loadDefaultRuleSets)

        private fun loadDefaultRuleSets(): List<String> {
            return ServiceLoader.load(
                RuleSetProvider::class.java,
                MissingRulesConfigValidator::class.java.classLoader
            )
                .filterIsInstance<DefaultRuleSetProvider>()
                .map { it.ruleSetId }
        }
    }
}

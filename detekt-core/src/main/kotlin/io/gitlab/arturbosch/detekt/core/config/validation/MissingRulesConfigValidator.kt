package io.gitlab.arturbosch.detekt.core.config.validation

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.SimpleNotification
import io.gitlab.arturbosch.detekt.core.config.YamlConfig
import java.util.ServiceLoader

internal class MissingRulesConfigValidator(
    private val baseline: YamlConfig,
    private val excludePatterns: Set<Regex>,
) : AbstractYamlConfigValidator() {

    override fun validate(
        configToValidate: YamlConfig,
        settings: ValidationSettings
    ): Collection<Notification> {
        if (!settings.checkExhaustiveness) {
            return emptyList()
        }
        return ruleSetNames.flatMap { ruleSet -> validateRuleSet(ruleSet, configToValidate) }
    }

    private fun validateRuleSet(
        ruleSet: String,
        configToValidate: YamlConfig,
    ): List<Notification> {
        val ruleSetConfigToValidate = configToValidate.getSubMapOrNull(ruleSet)
        val ruleSetConfigFromBaseline = baseline.getSubMapOrNull(ruleSet)
        return when {
            ruleSetConfigFromBaseline == null -> emptyList()
            ruleSetConfigToValidate == null -> listOf(ruleSetMissing(ruleSet))
            else -> checkForMissingRules(ruleSet, ruleSetConfigToValidate, ruleSetConfigFromBaseline)
        }
    }

    private fun checkForMissingRules(
        ruleSetName: String,
        ruleSetConfigToValidate: Map<String, Any>,
        ruleSetConfigFromBaseline: Map<String, Any>,
    ): List<Notification> {
        if (ruleSetConfigToValidate[Config.ACTIVE_KEY] == false) {
            return emptyList()
        }

        return ruleSetConfigFromBaseline.keys
            .filter { ruleNameCandidate -> excludePatterns.none { it.matches("$ruleSetName>$ruleNameCandidate") } }
            .filter { ruleName -> !ruleSetConfigToValidate.containsKey(ruleName) }
            .map { ruleName -> ruleMissing(ruleName, ruleSetName) }
    }

    private fun ruleMissing(ruleName: String, ruleSetName: String): Notification =
        SimpleNotification(
            "Rule '$ruleName' from the '$ruleSetName' rule set is missing in the configuration.",
            Notification.Level.Warning,
        )

    private fun ruleSetMissing(ruleSetName: String): Notification =
        SimpleNotification(
            "Rule set '$ruleSetName' is missing in the configuration.",
            Notification.Level.Warning,
        )

    @Suppress("UNCHECKED_CAST")
    private fun YamlConfig.getSubMapOrNull(propertyName: String) = properties[propertyName] as? Map<String, Any>

    companion object {

        private val ruleSetNames: List<String> by lazy(Companion::loadRuleSets)
        private fun loadRuleSets(): List<String> {
            return ServiceLoader.load(
                RuleSetProvider::class.java,
                MissingRulesConfigValidator::class.java.classLoader
            )
                .map { it.ruleSetId }
        }
    }
}

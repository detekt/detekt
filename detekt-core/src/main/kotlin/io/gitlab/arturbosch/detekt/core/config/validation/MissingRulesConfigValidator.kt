package io.gitlab.arturbosch.detekt.core.config.validation

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.core.config.YamlConfig
import io.gitlab.arturbosch.detekt.core.util.SimpleNotification
import java.util.ServiceLoader

internal class MissingRulesConfigValidator(
    private val baseline: YamlConfig,
    private val excludePatterns: Set<Regex>,
) : AbstractYamlConfigValidator() {

    override val id: String = "MissingRulesConfigValidator"

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
        ruleSet: RuleSet.Id,
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
        ruleSetName: RuleSet.Id,
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

    private fun ruleMissing(ruleName: String, ruleSetName: RuleSet.Id): Notification =
        SimpleNotification(
            "Rule '$ruleName' from the '$ruleSetName' rule set is missing in the configuration.",
            Notification.Level.Warning,
        )

    private fun ruleSetMissing(ruleSetName: RuleSet.Id): Notification =
        SimpleNotification(
            "Rule set '$ruleSetName' is missing in the configuration.",
            Notification.Level.Warning,
        )

    @Suppress("UNCHECKED_CAST")
    private fun YamlConfig.getSubMapOrNull(ruleSetId: RuleSet.Id) = properties[ruleSetId.value] as? Map<String, Any>

    companion object {

        private val ruleSetNames: List<RuleSet.Id> by lazy(Companion::loadRuleSets)
        private fun loadRuleSets(): List<RuleSet.Id> =
            ServiceLoader.load(
                RuleSetProvider::class.java,
                MissingRulesConfigValidator::class.java.classLoader
            )
                .map { it.ruleSetId }
    }
}

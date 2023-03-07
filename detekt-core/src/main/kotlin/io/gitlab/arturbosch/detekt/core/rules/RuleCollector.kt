package io.gitlab.arturbosch.detekt.core.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.core.ProcessingSettings

internal class RuleCollector(
    private val settings: ProcessingSettings,
    private val providers: List<RuleSetProvider>,
    private val config: Config,
) {
    fun run(): CollectedRules? = if (settings.spec.rulesSpec.listActiveRules) {
        collectActiveRules()
    } else {
        null
    }

    @Suppress("DEPRECATION")
    private fun collectActiveRules(): CollectedRules = CollectedRules(
        ruleSets = providers.map { provider ->
            val ruleSetConfig = config.subConfig(provider.ruleSetId)
            val ruleSet = provider.instance(ruleSetConfig)
            val rules = ruleSet.rules
                .flatMap { rule ->
                    when (rule) {
                        is Rule -> listOf(rule)
                        is MultiRule -> rule.rules
                        else -> error("Unknown rule type $rule")
                    }
                }
                .map { rule ->
                    CollectedRules.Rule(
                        id = rule.ruleId,
                        active = rule.active,
                    )
                }
            CollectedRules.RuleSet(
                id = provider.ruleSetId,
                active = ruleSetConfig.isActive(),
                rules = rules,
            )
        }
    )
}

internal fun logCollectedRules(settings: ProcessingSettings, collectedRules: CollectedRules) {
    settings.info("Currently active rules:")
    val rulesSets = collectedRules.ruleSets.filter { it.active }
    if (rulesSets.isEmpty()) settings.info("\t<no active rule sets>")
    rulesSets.forEach { ruleSet ->
        val rules = ruleSet.rules.filter { it.active }
        settings.info("\tRule set '${ruleSet.id}':")
        if (rules.isEmpty()) settings.info("\t\t<no active rules in the set>")
        rules.forEach { rule ->
            settings.info("\t\t${rule.id}")
        }
    }
    settings.info("") // empty line
}

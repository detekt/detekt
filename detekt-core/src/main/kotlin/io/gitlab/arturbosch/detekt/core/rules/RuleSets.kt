package io.gitlab.arturbosch.detekt.core.rules

import io.github.detekt.tooling.api.spec.RulesSpec
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.extensions.LIST_ITEM_SPACING
import java.util.ServiceLoader

fun ProcessingSettings.createRuleProviders(): List<RuleSetProvider> = when (val runPolicy = spec.rulesSpec.runPolicy) {
    RulesSpec.RunPolicy.NoRestrictions -> RuleSetLocator(this).load()

    RulesSpec.RunPolicy.DisableDefaultRuleSets ->
        RuleSetLocator(this).load()
            .filterNot { it is DefaultRuleSetProvider }

    is RulesSpec.RunPolicy.RestrictToSingleRule -> {
        val ruleSetId = runPolicy.ruleSetId
        val ruleName = runPolicy.ruleName
        val realProvider = requireNotNull(
            RuleSetLocator(this).load().find { it.ruleSetId == ruleSetId }
        ) { "There was no rule set with id '$ruleSetId'." }
        listOf(SingleRuleProvider(ruleName, realProvider))
    }
}

private class RuleSetLocator(private val settings: ProcessingSettings) {
    fun load(): List<RuleSetProvider> =
        ServiceLoader.load(RuleSetProvider::class.java, settings.pluginLoader)
            .filterNot { it.ruleSetId.value in settings.spec.extensionsSpec.disabledExtensions }
            .also { settings.debug { "Registered rule sets: $LIST_ITEM_SPACING${it.joinToString(LIST_ITEM_SPACING)}" } }
}

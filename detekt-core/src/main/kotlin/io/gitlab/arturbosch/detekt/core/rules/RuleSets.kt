package io.gitlab.arturbosch.detekt.core.rules

import io.github.detekt.tooling.api.spec.RulesSpec
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.extensions.LIST_ITEM_SPACING
import java.util.ServiceLoader

fun ProcessingSettings.createRuleProviders(): List<RuleSetProvider> {
    val ruleSetProviders = ServiceLoader.load(RuleSetProvider::class.java, pluginLoader)
        .filterNot { it.ruleSetId.value in spec.extensionsSpec.disabledExtensions }

    return when (val runPolicy = spec.rulesSpec.runPolicy) {
        RulesSpec.RunPolicy.NoRestrictions -> ruleSetProviders

        RulesSpec.RunPolicy.DisableDefaultRuleSets ->
            ruleSetProviders
                .filterNot { it is DefaultRuleSetProvider }

        is RulesSpec.RunPolicy.RestrictToSingleRule -> {
            val ruleSetId = runPolicy.ruleSetId
            val realProvider = requireNotNull(ruleSetProviders.find { it.ruleSetId == ruleSetId }) {
                "There was no rule set with id '$ruleSetId'."
            }
            listOf(SingleRuleProvider(runPolicy.ruleId.ruleName, realProvider))
        }
    }
        .also {
            debug {
                "Registered rule sets: $LIST_ITEM_SPACING" +
                    it.joinToString(LIST_ITEM_SPACING) { it.javaClass.canonicalName }
            }
        }
}

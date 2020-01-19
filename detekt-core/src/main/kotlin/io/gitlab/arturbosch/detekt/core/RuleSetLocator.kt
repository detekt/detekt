package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import java.util.ServiceLoader

class RuleSetLocator(private val settings: ProcessingSettings) {

    private val excludeDefaultRuleSets: Boolean = settings.excludeDefaultRuleSets

    fun load(): List<RuleSetProvider> =
        ServiceLoader.load(RuleSetProvider::class.java, settings.pluginLoader)
            .mapNotNull { it.nullIfDefaultAndExcluded() }
            .toList()

    private fun RuleSetProvider.nullIfDefaultAndExcluded() = if (excludeDefaultRuleSets && provided()) null else this

    private fun RuleSetProvider.provided() = ruleSetId in defaultRuleSetIds

    private val defaultRuleSetIds = listOf("comments", "complexity", "empty-blocks",
            "exceptions", "potential-bugs", "performance", "style", "naming")
}

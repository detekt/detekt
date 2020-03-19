package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider
import java.util.ServiceLoader

class RuleSetLocator(private val settings: ProcessingSettings) {

    private val excludeDefaultRuleSets: Boolean = settings.excludeDefaultRuleSets

    fun load(): List<RuleSetProvider> =
        ServiceLoader.load(RuleSetProvider::class.java, settings.pluginLoader)
            .mapNotNull { it.nullIfDefaultAndExcluded() }
            .toList()

    private fun RuleSetProvider.nullIfDefaultAndExcluded() =
        if (excludeDefaultRuleSets && this is DefaultRuleSetProvider) null else this
}

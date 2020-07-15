package io.gitlab.arturbosch.detekt.core.rules

import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.extensions.LIST_ITEM_SPACING
import java.util.ServiceLoader

class RuleSetLocator(private val settings: ProcessingSettings) {

    private val excludeDefaultRuleSets: Boolean = settings.spec.extensionsSpec.disableDefaultRuleSets

    fun load(): List<RuleSetProvider> =
        ServiceLoader.load(RuleSetProvider::class.java, settings.pluginLoader)
            .filterNot { it.ruleSetId in settings.spec.extensionsSpec.disabledExtensions }
            .filter(::shouldIncludeProvider)
            .also { settings.debug { "Registered rule sets: $LIST_ITEM_SPACING${it.joinToString(LIST_ITEM_SPACING)}" } }

    private fun shouldIncludeProvider(provider: RuleSetProvider) =
        !excludeDefaultRuleSets ||
            (excludeDefaultRuleSets && provider !is DefaultRuleSetProvider)
}

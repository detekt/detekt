package io.gitlab.arturbosch.detekt.core.rules

import io.github.detekt.psi.absolutePath
import io.github.detekt.tooling.api.spec.RulesSpec
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleId
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.createPathFilters
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import org.jetbrains.kotlin.psi.KtFile

internal fun Config.isActive(default: Boolean): Boolean = valueOrDefault(Config.ACTIVE_KEY, default)

fun Config.shouldAnalyzeFile(file: KtFile): Boolean {
    val filters = createPathFilters()
    return filters == null || !filters.isIgnored(file.absolutePath())
}

fun associateRuleIdsToRuleSetIds(ruleSets: List<RuleSet>): Map<RuleId, RuleSetId> {
    return ruleSets
        .flatMap { ruleSet ->
            ruleSet.rules.map { (ruleId, _) -> ruleId to ruleSet.id }
        }
        .toMap()
}

fun ProcessingSettings.createRuleProviders(): List<RuleSetProvider> = when (val runPolicy = spec.rulesSpec.runPolicy) {
    RulesSpec.RunPolicy.NoRestrictions -> RuleSetLocator(this).load()
    is RulesSpec.RunPolicy.RestrictToSingleRule -> {
        val (ruleSetId, ruleId) = runPolicy.id
        val realProvider = requireNotNull(
            RuleSetLocator(this).load().find { it.ruleSetId == ruleSetId }
        ) { "There was no rule set with id '$ruleSetId'." }
        listOf(SingleRuleProvider(ruleId, realProvider))
    }
}

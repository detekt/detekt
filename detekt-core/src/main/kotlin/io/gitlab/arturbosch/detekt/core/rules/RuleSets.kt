package io.gitlab.arturbosch.detekt.core.rules

import io.github.detekt.psi.absolutePath
import io.github.detekt.tooling.api.spec.RulesSpec
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleId
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.BaseRule
import io.gitlab.arturbosch.detekt.api.internal.createPathFilters
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

fun Config.isActive(): Boolean =
    valueOrDefault(Config.ACTIVE_KEY, true)

fun Config.shouldAnalyzeFile(file: KtFile): Boolean {
    val filters = createPathFilters()
    return filters == null || !filters.isIgnored(file.absolutePath())
}

fun RuleSet.visitFile(
    file: KtFile,
    bindingContext: BindingContext = BindingContext.EMPTY
): List<Finding> =
    rules.flatMap { rule ->
        rule.visitFile(file, bindingContext)
        rule.findings
    }

fun associateRuleIdsToRuleSetIds(ruleSets: Sequence<RuleSet>): Map<RuleId, RuleSetId> {
    fun extractIds(rule: BaseRule) =
        if (rule is MultiRule) {
            rule.rules.asSequence().map(Rule::ruleId)
        } else {
            sequenceOf(rule.ruleId)
        }
    return ruleSets.flatMap { ruleSet ->
        ruleSet.rules
            .asSequence()
            .flatMap { rule ->
                extractIds(rule).map { ruleId ->
                    ruleId to ruleSet.id
                }
            }
    }.toMap()
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

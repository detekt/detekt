package dev.detekt.core

import dev.detekt.api.Config
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.RuleInstance
import dev.detekt.api.RuleName
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider
import dev.detekt.api.Severity
import dev.detekt.api.internal.DefaultRuleSetProvider
import dev.detekt.api.internal.whichDetekt
import dev.detekt.api.valueOrNull
import dev.detekt.core.util.isActiveOrDefault
import dev.detekt.tooling.api.AnalysisMode
import java.net.URI

internal data class RuleDescriptor(
    val ruleProvider: (Config) -> Rule,
    val config: Config,
    val ruleInstance: RuleInstance,
)

internal fun getRules(
    analysisMode: AnalysisMode,
    ruleSetProviders: List<RuleSetProvider>,
    config: Config,
    log: (() -> String) -> Unit,
): List<RuleDescriptor> =
    ruleSetProviders.flatMap { ruleSetProvider ->
        val ruleSetConfig = config.subConfig(ruleSetProvider.ruleSetId.value)
        val urlGenerator: (Rule) -> URI? =
            if (ruleSetProvider is DefaultRuleSetProvider ||
                ruleSetProvider.ruleSetId.value in externalFirstPartyRuleSets
            ) {
                { rule -> rule.url ?: generateDefaultUrl(ruleSetProvider.ruleSetId, rule.ruleName) }
            } else {
                { rule -> rule.url }
            }
        ruleSetProvider.instance().getRules(ruleSetConfig, analysisMode, urlGenerator, log)
    }

private fun RuleSet.getRules(
    config: Config,
    analysisMode: AnalysisMode,
    urlGenerator: (Rule) -> URI?,
    log: (() -> String) -> Unit,
): Sequence<RuleDescriptor> =
    config.subConfigKeys()
        .asSequence()
        .mapNotNull { ruleId -> extractRuleName(ruleId)?.let { ruleName -> ruleId to ruleName } }
        .mapNotNull { (ruleId, ruleName) ->
            this.rules[ruleName]?.let { ruleProvider ->
                val rule = ruleProvider(Config.empty)
                val ruleConfig = config.subConfig(ruleId)
                val active = config.isActiveOrDefault(true) && ruleConfig.isActiveOrDefault(false)
                val executable = when (analysisMode) {
                    AnalysisMode.full -> true
                    AnalysisMode.light -> rule !is RequiresAnalysisApi
                }
                if (active && !executable) {
                    log { "The rule '$ruleId' requires type resolution but it was run without it." }
                }
                RuleDescriptor(
                    ruleProvider = ruleProvider,
                    config = ruleConfig,
                    ruleInstance = RuleInstance(
                        id = ruleId,
                        ruleSetId = id,
                        url = urlGenerator(rule),
                        description = rule.description,
                        severity = ruleConfig.computeSeverity(),
                        active = active && executable,
                    )
                )
            }
        }

private fun generateDefaultUrl(ruleSetId: RuleSetId, ruleName: RuleName) =
    URI("https://detekt.dev/docs/${whichDetekt()}/rules/${ruleSetId.value.lowercase()}#${ruleName.value.lowercase()}")

private val externalFirstPartyRuleSets = setOf(
    "ktlint",
    "ruleauthors",
    "libraries",
)

/**
 * Compute severity in the priority order:
 * - Severity of the rule
 * - Severity of the parent ruleset
 * - Default severity
 */
private fun Config.computeSeverity(): Severity {
    val configValue: String = valueOrNull(Config.SEVERITY_KEY)
        ?: parent?.valueOrNull(Config.SEVERITY_KEY)
        ?: return Severity.Error
    return parseToSeverity(configValue)
}

private fun parseToSeverity(severity: String): Severity {
    val lowercase = severity.lowercase()
    return Severity.entries.find { it.name.lowercase() == lowercase }
        ?: error("'$severity' is not a valid Severity. Allowed values are ${Severity.entries}")
}

internal fun extractRuleName(key: String): RuleName? = runCatching { RuleName(key.substringBefore("/")) }.getOrNull()

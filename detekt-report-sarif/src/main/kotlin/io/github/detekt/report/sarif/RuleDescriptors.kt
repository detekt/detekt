package io.github.detekt.report.sarif

import io.github.detekt.sarif4k.MultiformatMessageString
import io.github.detekt.sarif4k.ReportingConfiguration
import io.github.detekt.sarif4k.ReportingDescriptor
import io.gitlab.arturbosch.detekt.api.BaseRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.SeverityLevel
import java.util.Locale
import java.util.ServiceLoader

/**
 * Given the existing config, return a list of [ReportingDescriptor] for the rules.
 */
internal fun toReportingDescriptors(config: Config): List<ReportingDescriptor> {
    val sets =
        ServiceLoader.load(RuleSetProvider::class.java, SarifOutputReport::class.java.classLoader)
            .map { it.instance(config.subConfig(it.ruleSetId)) }

    val ruleSetIdAndRules = sets.flatMap { ruleSet ->
        val ruleSetConfig = config.subConfig(ruleSet.id)

        ruleSet.rules.map { rule ->
            RuleInfo(ruleSet.id, rule, ruleSetConfig)
        }
    }

    val descriptors = mutableListOf<ReportingDescriptor>()
    ruleSetIdAndRules.forEach { (ruleSetId, rule, ruleSetConfig) ->
        @Suppress("DEPRECATION")
        when (rule) {
            is io.gitlab.arturbosch.detekt.api.MultiRule -> {
                val multiRuleConfig = ruleSetConfig.subConfig(rule.ruleId)

                descriptors.addAll(
                    rule.toDescriptors(
                        ruleSetId,
                        multiRuleConfig
                    )
                )
            }
            is Rule -> {
                val ruleConfig = ruleSetConfig.subConfig(rule.ruleId)
                descriptors.add(rule.toDescriptor(ruleSetId, ruleConfig))
            }
        }
    }
    return descriptors
}

@Suppress("DEPRECATION")
private fun io.gitlab.arturbosch.detekt.api.MultiRule.toDescriptors(
    ruleSetId: RuleSetId,
    multiRuleConfig: Config
): List<ReportingDescriptor> =
    this.rules.map {
        val config = multiRuleConfig.subConfig(it.ruleId)
        it.toDescriptor(ruleSetId, config)
    }

private fun Rule.toDescriptor(ruleSetId: RuleSetId, config: Config): ReportingDescriptor {
    val formattedRuleSetId = ruleSetId.lowercase(Locale.US)
    val formattedRuleId = ruleId.lowercase(Locale.US)
    val severity = computeSeverity(config)

    return ReportingDescriptor(
        id = "detekt.$ruleSetId.$ruleId",
        name = ruleId,
        shortDescription = MultiformatMessageString(text = issue.description),
        helpURI = "https://detekt.dev/$formattedRuleSetId.html#$formattedRuleId",
        defaultConfiguration = ReportingConfiguration(
            level = severity.toResultLevel()
        )
    )
}

private data class RuleInfo(
    val ruleSetId: String,
    val rule: BaseRule,
    val parentConfig: Config
)

private fun Rule.computeSeverity(config: Config): SeverityLevel {
    val configValue: String = config.valueOrNull(Config.SEVERITY_KEY)
        ?: return SeverityLevel.ERROR
    return parseToSeverity(configValue)
}

internal fun parseToSeverity(severity: String): SeverityLevel {
    val lowercase = severity.lowercase()
    return SeverityLevel.entries.find { it.name.lowercase() == lowercase }
        ?: error("$severity is not a valid Severity. Allowed values are ${SeverityLevel.entries}")
}

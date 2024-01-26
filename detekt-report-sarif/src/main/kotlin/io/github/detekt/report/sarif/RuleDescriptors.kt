package io.github.detekt.report.sarif

import io.github.detekt.sarif4k.MultiformatMessageString
import io.github.detekt.sarif4k.ReportingConfiguration
import io.github.detekt.sarif4k.ReportingDescriptor
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.Severity
import java.util.ServiceLoader

/**
 * Given the existing config, return a list of [ReportingDescriptor] for the rules.
 */
internal fun toReportingDescriptors(config: Config): List<ReportingDescriptor> {
    val sets = ServiceLoader.load(RuleSetProvider::class.java, SarifOutputReport::class.java.classLoader)
        .map { it.instance() }
    val ruleSetIdAndRules = sets.flatMap { ruleSet ->
        val ruleSetConfig = config.subConfig(ruleSet.id.value)
        ruleSet.rules.map { (id, provider) ->
            val rule = provider(ruleSetConfig.subConfig(id.value))
            val severity = rule.computeSeverity()
            RuleInfo(ruleSet.id, rule.issue, severity)
        }
    }
    val descriptors = mutableListOf<ReportingDescriptor>()
    ruleSetIdAndRules.forEach { (ruleSetId, issue, severity) ->
        descriptors.add(issue.toDescriptor(ruleSetId, severity))
    }
    return descriptors
}

private fun Issue.toDescriptor(ruleSetId: RuleSet.Id, severity: Severity): ReportingDescriptor {
    val formattedRuleSetId = ruleSetId.value.lowercase()
    val formattedRuleId = id.value.lowercase()

    return ReportingDescriptor(
        id = "detekt.$ruleSetId.$id",
        name = id.value,
        shortDescription = MultiformatMessageString(text = description),
        helpURI = "https://detekt.dev/$formattedRuleSetId.html#$formattedRuleId",
        defaultConfiguration = ReportingConfiguration(
            level = severity.toResultLevel()
        )
    )
}

private data class RuleInfo(
    val id: RuleSet.Id,
    val severity: Issue,
    val issue: Severity
)

private fun Rule.computeSeverity(): Severity {
    val configValue: String = config.valueOrNull(Config.SEVERITY_KEY)
        ?: config.parent?.valueOrNull(Config.SEVERITY_KEY)
        ?: return Severity.Error
    return parseToSeverity(configValue)
}

internal fun parseToSeverity(severity: String): Severity {
    val lowercase = severity.lowercase()
    return Severity.entries.find { it.name.lowercase() == lowercase }
        ?: error("$severity is not a valid Severity. Allowed values are ${Severity.entries}")
}

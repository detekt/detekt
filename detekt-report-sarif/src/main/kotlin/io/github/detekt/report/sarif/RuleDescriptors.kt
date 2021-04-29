package io.github.detekt.report.sarif

import io.github.detekt.sarif4k.MultiformatMessageString
import io.github.detekt.sarif4k.ReportingDescriptor
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import java.util.Locale
import java.util.ServiceLoader

/**
 * Given the existing config, return a list of [ReportingDescriptor] for the rules.
 */
fun toReportingDescriptors(config: Config): List<ReportingDescriptor> {
    val sets = ServiceLoader.load(RuleSetProvider::class.java, SarifOutputReport::class.java.classLoader)
        .map { it.instance(config.subConfig(it.ruleSetId)) }
    val ruleSetIdAndRules = sets.flatMap { ruleSet ->
        ruleSet.rules.map { rule ->
            ruleSet.id to rule
        }
    }
    val descriptors = mutableListOf<ReportingDescriptor>()
    ruleSetIdAndRules.forEach { (ruleSetId, rule) ->
        when (rule) {
            is MultiRule ->
                descriptors.addAll(rule.toDescriptors(ruleSetId))
            is Rule ->
                descriptors.add(rule.toDescriptor(ruleSetId))
        }
    }
    return descriptors
}

private fun MultiRule.toDescriptors(ruleSetId: RuleSetId): List<ReportingDescriptor> =
    this.rules.map { it.toDescriptor(ruleSetId) }

private fun Rule.toDescriptor(ruleSetId: RuleSetId): ReportingDescriptor {
    val formattedRuleSetId = ruleSetId.toLowerCase(Locale.US)
    val formattedRuleId = ruleId.toLowerCase(Locale.US)

    return ReportingDescriptor(
        id = "detekt.$ruleSetId.$ruleId",
        name = ruleId,
        shortDescription = MultiformatMessageString(text = issue.description),
        helpURI = "https://detekt.github.io/detekt/$formattedRuleSetId.html#$formattedRuleId"
    )
}

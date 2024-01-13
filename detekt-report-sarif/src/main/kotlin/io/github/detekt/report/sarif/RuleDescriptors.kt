package io.github.detekt.report.sarif

import io.github.detekt.sarif4k.MultiformatMessageString
import io.github.detekt.sarif4k.ReportingDescriptor
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import java.util.ServiceLoader

/**
 * Given the existing config, return a list of [ReportingDescriptor] for the rules.
 */
internal fun toReportingDescriptors(): List<ReportingDescriptor> {
    val sets = ServiceLoader.load(RuleSetProvider::class.java, SarifOutputReport::class.java.classLoader)
        .map { it.instance() }
    val ruleSetIdAndRules = sets.flatMap { ruleSet ->
        ruleSet.rules.map { (_, provider) ->
            ruleSet.id to provider(Config.empty).issue
        }
    }
    val descriptors = mutableListOf<ReportingDescriptor>()
    ruleSetIdAndRules.forEach { (ruleSetId, issue) ->
        descriptors.add(issue.toDescriptor(ruleSetId))
    }
    return descriptors
}

private fun Issue.toDescriptor(ruleSetId: RuleSet.Id): ReportingDescriptor {
    val formattedRuleSetId = ruleSetId.value.lowercase()
    val formattedRuleId = id.value.lowercase()

    return ReportingDescriptor(
        id = "detekt.$ruleSetId.$id",
        name = id.value,
        shortDescription = MultiformatMessageString(text = description),
        helpURI = "https://detekt.dev/$formattedRuleSetId.html#$formattedRuleId"
    )
}

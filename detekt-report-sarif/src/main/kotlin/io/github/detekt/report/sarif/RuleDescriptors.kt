package io.github.detekt.report.sarif

import io.github.detekt.sarif4j.MultiformatMessageString
import io.github.detekt.sarif4j.ReportingDescriptor
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import java.net.URI
import java.util.Locale
import java.util.ServiceLoader

fun ruleDescriptors(config: Config): HashMap<String, ReportingDescriptor> {
    val sets = ServiceLoader.load(RuleSetProvider::class.java, SarifOutputReport::class.java.classLoader)
        .map { it.instance(config.subConfig(it.ruleSetId)) }
    val descriptors = HashMap<String, ReportingDescriptor>()
    for (ruleSet in sets) {
        for (rule in ruleSet.rules) {
            when (rule) {
                is MultiRule -> {
                    descriptors.putAll(rule.toDescriptors(ruleSet.id).associateBy { it.name })
                }
                is Rule -> {
                    val descriptor = rule.toDescriptor(ruleSet.id)
                    descriptors[descriptor.name] = descriptor
                }
            }
        }
    }
    return descriptors
}

fun descriptor(init: ReportingDescriptor.() -> Unit) = ReportingDescriptor().apply(init)

fun MultiRule.toDescriptors(ruleSetId: RuleSetId): List<ReportingDescriptor> =
    this.rules.map { it.toDescriptor(ruleSetId) }

fun Rule.toDescriptor(ruleSetId: RuleSetId): ReportingDescriptor = descriptor {
    id = "detekt.$ruleSetId.$ruleId"
    name = ruleId
    shortDescription = MultiformatMessageString().apply { text = issue.description }
    helpUri = URI.create(
        "https://detekt.github.io/detekt/${ruleSetId.toLowerCase(Locale.US)}.html#${ruleId.toLowerCase(Locale.US)}"
    )
}

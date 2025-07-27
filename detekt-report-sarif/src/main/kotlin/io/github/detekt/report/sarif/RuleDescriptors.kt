package io.github.detekt.report.sarif

import dev.detekt.api.RuleInstance
import io.github.detekt.sarif4k.MultiformatMessageString
import io.github.detekt.sarif4k.ReportingConfiguration
import io.github.detekt.sarif4k.ReportingDescriptor

internal fun RuleInstance.toDescriptor() = ReportingDescriptor(
    id = "detekt.$ruleSetId.$id",
    name = id,
    shortDescription = MultiformatMessageString(text = description),
    helpURI = url?.toString(),
    defaultConfiguration = ReportingConfiguration(
        enabled = active,
        level = severity.toResultLevel(),
    )
)

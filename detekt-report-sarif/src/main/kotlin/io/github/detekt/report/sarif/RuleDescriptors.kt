package io.github.detekt.report.sarif

import io.github.detekt.sarif4k.MultiformatMessageString
import io.github.detekt.sarif4k.ReportingConfiguration
import io.github.detekt.sarif4k.ReportingDescriptor
import dev.detekt.api.RuleInstance

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
